package com.tcs.Library.controllers;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.DonationApprovalRequest;
import com.tcs.Library.dto.DonationRequest;
import com.tcs.Library.entity.BookDonation;
import com.tcs.Library.entity.User;
import com.tcs.Library.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookDonation>> submitDonation(
            @AuthenticationPrincipal User user,
            @RequestBody DonationRequest request) {
        BookDonation donation = donationService.submitDonation(user.getPublicId(), request);
        return ResponseEntity.ok(ApiResponse.success("Donation request submitted", donation));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BookDonation>>> getMyDonations(@AuthenticationPrincipal User user) {
        List<BookDonation> donations = donationService.getUserDonations(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Your donations retrieved", donations));
    }
}
