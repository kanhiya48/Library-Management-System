package com.tcs.Library.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User profile response DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String publicId;
    private String customerName;
    private String email;
    private String countryCode;
    private String mobileNumber;
    private String address;
    private LocalDate dateOfBirth;
    private String role;

    // Borrowing info
    private int currentBorrows;
    private int totalBorrowHistory;
    private int overdueBooks;

    // Fine info
    private BigDecimal totalUnpaidFine;
    private boolean isDefaulter;

    // Stats
    private LocalDate memberSince;
}
