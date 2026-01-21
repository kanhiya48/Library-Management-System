package com.tcs.Library.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dashboard statistics for admin view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {

    // Book Statistics
    private long totalBooks;
    private long totalBookCopies;
    private long availableCopies;
    private long borrowedCopies;

    // User Statistics
    private long totalUsers;
    private long totalStaff;
    private long totalAdmins;
    private long defaulterUsers;

    // Borrow Statistics
    private long activeLoans;
    private long overdueBooks;
    private long totalLoansToday;
    private long totalReturnsToday;

    // Fine Statistics
    private BigDecimal totalFinesCollected;
    private BigDecimal totalUnpaidFines;
    private long unpaidFineCount;

    // Donation Statistics
    private long pendingDonations;
    private long approvedDonationsTotal;

    // Complaint Statistics
    private long pendingComplaints;
    private long escalatedComplaints;
    private long resolvedComplaintsToday;

    // Author Statistics
    private long totalAuthors;
}
