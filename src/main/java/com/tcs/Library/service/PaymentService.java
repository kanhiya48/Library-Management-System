package com.tcs.Library.service;

import com.tcs.Library.dto.PaymentHistoryResponse;
import com.tcs.Library.dto.PaymentResponse;
import com.tcs.Library.entity.Fine;
import com.tcs.Library.entity.Payment;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.PaymentMethod;
import com.tcs.Library.repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepo paymentRepo;

    /**
     * Create a payment record for one or more fines
     */
    @Transactional
    public Payment createPayment(User user, List<Fine> fines, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setFines(fines);
        payment.setPaymentMethod(paymentMethod);

        // Calculate total amount
        BigDecimal totalAmount = fines.stream()
                .map(Fine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        payment.setTotalAmount(totalAmount);

        // Generate receipt number (format: RCP-YYYYMMDD-XXXX)
        payment.setReceiptNumber(generateReceiptNumber());

        // For now, transaction ID can be null or set to payment ID
        // In real implementation, this would come from payment gateway

        Payment savedPayment = paymentRepo.save(payment);
        log.info("Payment created: {} for user: {} with {} fines",
                savedPayment.getPaymentId(), user.getEmail(), fines.size());

        return savedPayment;
    }

    /**
     * Get payment history for a user
     */
    public List<PaymentHistoryResponse> getUserPaymentHistory(Long userId) {
        List<Payment> payments = paymentRepo.findByUserIdOrderByPaymentDateDesc(userId);

        return payments.stream()
                .map(this::toPaymentHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get detailed payment information by payment ID
     */
    public PaymentResponse getPaymentDetails(String paymentId) {
        Payment payment = paymentRepo.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        return toPaymentResponse(payment);
    }

    /**
     * Generate a unique receipt number
     */
    private String generateReceiptNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = paymentRepo.count() + 1;
        String sequencePart = String.format("%04d", count);
        return "RCP-" + datePart + "-" + sequencePart;
    }

    /**
     * Convert Payment entity to PaymentHistoryResponse DTO
     */
    private PaymentHistoryResponse toPaymentHistoryResponse(Payment payment) {
        return PaymentHistoryResponse.builder()
                .paymentId(payment.getPaymentId())
                .receiptNumber(payment.getReceiptNumber())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .totalAmount(payment.getTotalAmount())
                .fineCount(payment.getFines().size())
                .build();
    }

    /**
     * Convert Payment entity to PaymentResponse DTO
     */
    private PaymentResponse toPaymentResponse(Payment payment) {
        User user = payment.getUser();

        List<Long> fineIds = payment.getFines().stream()
                .map(Fine::getId)
                .collect(Collectors.toList());

        List<PaymentResponse.FineDetail> fineDetails = payment.getFines().stream()
                .map(fine -> PaymentResponse.FineDetail.builder()
                        .fineId(fine.getId())
                        .amount(fine.getAmount())
                        .reason(fine.getIssuedBook() != null &&
                                fine.getIssuedBook().getBookCopy() != null &&
                                fine.getIssuedBook().getBookCopy().getBook() != null
                                        ? "Late return - " + fine.getIssuedBook().getBookCopy().getBook().getBookTitle()
                                        : "Fine")
                        .build())
                .collect(Collectors.toList());

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .receiptNumber(payment.getReceiptNumber())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .totalAmount(payment.getTotalAmount())
                .userName(user.getCustomerName())
                .userEmail(user.getEmail())
                .fineIds(fineIds)
                .fineDetails(fineDetails)
                .build();
    }
}
