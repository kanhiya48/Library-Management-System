package com.tcs.Library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tcs.Library.dto.*;
import com.tcs.Library.service.AuthService;
import lombok.RequiredArgsConstructor;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {
    @Autowired
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            return ResponseEntity.ok(authService.login(loginRequest));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.badRequest().body("Incorrect Credentials");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signup(@RequestBody UserRegRequest signuprequest) {
        return ResponseEntity.ok(authService.signup(signuprequest));
    }

    /**
     * FORGOT PASSWORD - Step 1: Get secret question
     * POST /auth/forgot-password
     * Body: { "email": "user@example.com" }
     * Response: { "success": true, "email": "...", "secretQuestion": "What is your
     * pet's name?" }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<SecretQuestionResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        System.out.println("Forgot Password Request for email: " + request.getEmail());
        try {
            String question = authService.getSecretQuestion(request.getEmail());
            return ResponseEntity.ok(SecretQuestionResponse.success(request.getEmail(), question));
        } catch (Exception ex) {
            System.out.println("Error in forgotPassword: " + ex.getMessage());
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(SecretQuestionResponse.error(ex.getMessage()));
        }
    }

    /**
     * FORGOT PASSWORD - Step 2: Verify secret answer
     * POST /auth/verify-answer
     * Body: { "email": "user@example.com", "secretAnswer": "fluffy" }
     * Response: { "success": true, "message": "Answer verified" }
     */
    @PostMapping("/verify-answer")
    public ResponseEntity<ApiResponse<String>> verifyAnswer(@RequestBody VerifyAnswerRequest request) {
        try {
            boolean verified = authService.verifySecretAnswer(request.getEmail(), request.getSecretAnswer());
            if (verified) {
                return ResponseEntity
                        .ok(ApiResponse.success("Answer verified. You can now reset your password.", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Incorrect answer. Please try again."));
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    /**
     * FORGOT PASSWORD - Step 3: Reset password
     * POST /auth/reset-password
     * Body: { "email": "...", "secretAnswer": "...", "newPassword": "...",
     * "confirmNewPassword": "..." }
     * Response: { "success": true, "message": "Password reset successfully" }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            // Validate passwords match
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Passwords do not match"));
            }

            authService.resetPassword(request.getEmail(), request.getSecretAnswer(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse
                    .success("Password reset successfully. You can now login with your new password.", null));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Test");
    }
}
