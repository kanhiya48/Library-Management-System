package com.tcs.Library.service;

import com.tcs.Library.entity.Fine;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.PaymentMethod;
import com.tcs.Library.repository.FineRepo;
import com.tcs.Library.repository.IssuedBooksRepo;
import com.tcs.Library.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FineService {

    private final FineRepo fineRepo;
    private final UserRepo userRepo;
    private final IssuedBooksRepo issuedBooksRepo;
    private final PaymentService paymentService;

    private static final BigDecimal DEFAULTER_FINE_THRESHOLD = new BigDecimal("100.00");
    private static final int DEFAULTER_OVERDUE_DAYS = 30;

    public List<Fine> getUserFines(Long userId) {
        return fineRepo.findByUserId(userId);
    }

    public List<Fine> getUnpaidFines(Long userId) {
        return fineRepo.findByUserIdAndIsPaid(userId, false);
    }

    @Transactional
    public Fine payFine(Long fineId) {
        Fine fine = fineRepo.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found: " + fineId));

        if (fine.isPaid()) {
            throw new RuntimeException("Fine is already paid");
        }

        fine.setPaid(true);
        fine.setPaidAt(LocalDate.now());
        fineRepo.save(fine);

        // Update user's total unpaid fine
        User user = fine.getUser();
        BigDecimal newTotal = fineRepo.getTotalUnpaidFineByUser(user.getId());
        user.setTotalUnpaidFine(newTotal);

        // Recalculate defaulter status
        recalculateDefaulterStatus(user);
        userRepo.save(user);

        // Create payment record (background operation)
        try {
            paymentService.createPayment(user, Collections.singletonList(fine), PaymentMethod.ONLINE);
            log.info("Payment record created for fine {}", fineId);
        } catch (Exception e) {
            log.error("Failed to create payment record for fine {}: {}", fineId, e.getMessage());
            // Don't fail the fine payment if payment record creation fails
        }

        log.info("Fine {} paid by user {}", fineId, user.getEmail());
        return fine;
    }

    public void recalculateDefaulterStatus(User user) {
        boolean shouldBeDefaulter = false;

        // Check unpaid fines
        if (user.getTotalUnpaidFine().compareTo(DEFAULTER_FINE_THRESHOLD) > 0) {
            shouldBeDefaulter = true;
        }

        // Check severely overdue books
        LocalDate cutoffDate = LocalDate.now().minusDays(DEFAULTER_OVERDUE_DAYS);
        var overdueBooks = issuedBooksRepo.findByUserIdAndStatus(user.getId(), "BORROWED")
                .stream()
                .filter(ib -> ib.getDueDate().isBefore(cutoffDate))
                .toList();

        if (!overdueBooks.isEmpty()) {
            shouldBeDefaulter = true;
        }

        user.setDefaulter(shouldBeDefaulter);

        if (!shouldBeDefaulter && user.isDefaulter()) {
            log.info("User {} removed from defaulter list", user.getEmail());
        }
    }
}
