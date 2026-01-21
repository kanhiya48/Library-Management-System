package com.tcs.Library.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.tcs.Library.dto.DashboardStats;
import com.tcs.Library.enums.BookStatus;
import com.tcs.Library.enums.ComplaintStatus;
import com.tcs.Library.enums.DonationStatus;
import com.tcs.Library.enums.Role;
import com.tcs.Library.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for generating admin dashboard statistics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final BookRepo bookRepo;
    private final BookCopyRepo bookCopyRepo;
    private final UserRepo userRepo;
    private final IssuedBooksRepo issuedBooksRepo;
    private final FineRepo fineRepo;
    private final BookDonationRepo donationRepo;
    private final ComplaintRepo complaintRepo;
    private final AuthorRepo authorRepo;

    /**
     * Get comprehensive dashboard statistics.
     */
    public DashboardStats getDashboardStats() {
        LocalDate today = LocalDate.now();

        return DashboardStats.builder()
                // Book stats
                .totalBooks(bookRepo.count())
                .totalBookCopies(bookCopyRepo.count())
                .availableCopies(bookCopyRepo.countByStatus(BookStatus.AVAILABLE))
                .borrowedCopies(bookCopyRepo.countByStatus(BookStatus.BORROWED))

                // User stats
                .totalUsers(userRepo.countByRole(Role.USER))
                .totalStaff(userRepo.countByRole(Role.STAFF))
                .totalAdmins(userRepo.countByRole(Role.ADMIN))
                .defaulterUsers(userRepo.countByIsDefaulter(true))

                // Borrow stats
                .activeLoans(issuedBooksRepo.countByStatus("BORROWED"))
                .overdueBooks(issuedBooksRepo.countOverdueBooks(today))
                .totalLoansToday(issuedBooksRepo.countByIssueDateAndStatus(today, "BORROWED")
                        + issuedBooksRepo.countByIssueDateAndStatus(today, "RETURNED"))
                .totalReturnsToday(issuedBooksRepo.countByReturnDate(today))

                // Fine stats
                .totalFinesCollected(
                        fineRepo.getTotalPaidFines() != null ? fineRepo.getTotalPaidFines() : BigDecimal.ZERO)
                .totalUnpaidFines(
                        fineRepo.getTotalUnpaidFines() != null ? fineRepo.getTotalUnpaidFines() : BigDecimal.ZERO)
                .unpaidFineCount(fineRepo.countByIsPaid(false))

                // Donation stats
                .pendingDonations(donationRepo.countByStatus(DonationStatus.PENDING))
                .approvedDonationsTotal(donationRepo.countByStatus(DonationStatus.APPROVED))

                // Complaint stats
                .pendingComplaints(complaintRepo.countByStatus(ComplaintStatus.PENDING)
                        + complaintRepo.countByStatus(ComplaintStatus.ASSIGNED)
                        + complaintRepo.countByStatus(ComplaintStatus.IN_PROGRESS))
                .escalatedComplaints(complaintRepo.countByStatus(ComplaintStatus.ESCALATED_TO_ADMIN))
                .resolvedComplaintsToday(complaintRepo.countResolvedToday(today))

                // Author stats
                .totalAuthors(authorRepo.count())
                .build();
    }

    /**
     * Get quick summary stats for a mini dashboard.
     */
    public DashboardStats getQuickStats() {
        LocalDate today = LocalDate.now();

        return DashboardStats.builder()
                .totalBooks(bookRepo.count())
                .activeLoans(issuedBooksRepo.countByStatus("BORROWED"))
                .overdueBooks(issuedBooksRepo.countOverdueBooks(today))
                .pendingComplaints(complaintRepo.countByStatus(ComplaintStatus.PENDING))
                .totalUnpaidFines(
                        fineRepo.getTotalUnpaidFines() != null ? fineRepo.getTotalUnpaidFines() : BigDecimal.ZERO)
                .build();
    }
}
