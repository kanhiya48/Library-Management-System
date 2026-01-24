package com.tcs.Library.controllers;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.PaymentHistoryResponse;
import com.tcs.Library.dto.PaymentResponse;
import com.tcs.Library.entity.User;
import com.tcs.Library.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment-history")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Get payment history for the current authenticated user
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentHistoryResponse>>> getPaymentHistory(
            @AuthenticationPrincipal User user) {
        List<PaymentHistoryResponse> paymentHistory = paymentService.getUserPaymentHistory(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Payment history retrieved", paymentHistory));
    }

    /**
     * Get detailed payment information by payment ID
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentDetails(
            @PathVariable String paymentId,
            @AuthenticationPrincipal User user) {
        PaymentResponse paymentDetails = paymentService.getPaymentDetails(paymentId);

        // Verify the payment belongs to the requesting user (security check)
        if (!paymentDetails.getUserEmail().equals(user.getEmail())) {
            return ResponseEntity.status(403)
                    .body(ApiResponse.error("You don't have permission to view this payment"));
        }

        return ResponseEntity.ok(ApiResponse.success("Payment details retrieved", paymentDetails));
    }
}
