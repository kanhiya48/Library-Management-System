package com.tcs.Library.controllers;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.UserProfileResponse;
import com.tcs.Library.dto.UserUpdateRequest;
import com.tcs.Library.entity.User;
import com.tcs.Library.repository.IssuedBooksRepo;
import com.tcs.Library.repository.UserRepo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for user profile management.
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepo userRepo;
    private final IssuedBooksRepo issuedBooksRepo;

    /**
     * Get current user's profile.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User user) {

        UserProfileResponse profile = buildProfileResponse(user);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profile));
    }

    /**
     * Update current user's profile.
     */
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserUpdateRequest request) {

        // Update only non-null fields
        if (request.getCustomerName() != null) {
            user.setCustomerName(request.getCustomerName());
        }
        if (request.getCountryCode() != null) {
            user.setCountryCode(request.getCountryCode());
        }
        if (request.getMobileNumber() != null) {
            user.setMobileNumber(request.getMobileNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }

        userRepo.save(user);

        UserProfileResponse profile = buildProfileResponse(user);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    /**
     * Build profile response from user entity.
     */
    private UserProfileResponse buildProfileResponse(User user) {
        int currentBorrows = issuedBooksRepo.countByUserIdAndStatus(user.getId(), "BORROWED");
        int totalHistory = issuedBooksRepo.findByUserId(user.getId()).size();
        int overdueCount = issuedBooksRepo.findOverdueBooksForUser(user.getId(), LocalDate.now()).size();

        // Get roles as comma-separated string
        String rolesStr = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));

        return UserProfileResponse.builder()
                .publicId(user.getPublicId().toString())
                .customerName(user.getCustomerName())
                .email(user.getEmail())
                .countryCode(user.getCountryCode())
                .mobileNumber(user.getMobileNumber())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .role(rolesStr)
                .currentBorrows(currentBorrows)
                .totalBorrowHistory(totalHistory)
                .overdueBooks(overdueCount)
                .totalUnpaidFine(user.getTotalUnpaidFine())
                .isDefaulter(user.isDefaulter())
                .memberSince(null) // User entity doesn't have createdAt
                .build();
    }
}
