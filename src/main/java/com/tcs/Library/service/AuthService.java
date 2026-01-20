package com.tcs.Library.service;

import com.tcs.Library.config.CustomUserDetailService;
import java.time.Clock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.tcs.Library.dto.LoginRequest;
import com.tcs.Library.dto.LoginResponse;
import com.tcs.Library.dto.SignUpResponse;
import com.tcs.Library.dto.UserRegRequest;
import com.tcs.Library.dto.wrapper.UserMapper;
import com.tcs.Library.enums.Role;
import com.tcs.Library.error.NoUserFoundException;
import com.tcs.Library.repository.UserRepo;
import com.tcs.Library.utils.AuthUtils;
import com.tcs.Library.utils.UserValidations;
import com.tcs.Library.utils.UserValidations.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private final AuthenticationManager authManager;
    @Autowired
    private final AuthUtils authUtils;
    @Autowired
    private UserRepo userDS;
    @Autowired
    private UserValidations res;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailService detailService;

    public LoginResponse login(LoginRequest req) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(req.getEmail(),
                req.getPassword());

        authManager.authenticate(authToken);

        UserDetails userDetails = detailService.loadUserByUsername(req.getEmail());

        String token = authUtils.generateAccessToken(userDetails.getUsername(),
                userDetails.getAuthorities());

        return new LoginResponse(token);
    }

    public SignUpResponse signup(UserRegRequest req) {
        ValidationResult result = res.validateRegistration(req, Clock.systemDefaultZone());
        if (result.isValid()) {
            com.tcs.Library.entity.User newUser = UserMapper.toEntity(req);
            newUser.setPasswordHash(passwordEncoder.encode(req.getPassword()));
            // Hash and store secret answer for password recovery
            newUser.setSecretAnswerHash(passwordEncoder.encode(req.getSecretAnswer().toLowerCase().trim()));
            if (newUser.getRoles().isEmpty()) {
                newUser.getRoles().add(Role.USER);
            }
            userDS.save(newUser);
            return new SignUpResponse(newUser.getPublicId());
        }

        // Convert validation errors to a map for structured response
        java.util.Map<String, String> fieldErrors = new java.util.LinkedHashMap<>();
        for (UserValidations.FieldError error : result.getErrors()) {
            fieldErrors.put(error.getField(), error.getMessage());
        }

        throw new com.tcs.Library.error.ValidationException("Validation failed", fieldErrors);
    }

    /**
     * Step 1: Verify email and get secret question
     * Returns the secret question for the user
     */
    public String getSecretQuestion(String email) {
        com.tcs.Library.entity.User user = userDS.findByEmail(email)
                .orElseThrow(() -> new NoUserFoundException("No account found with email: " + email));
        return user.getSecretQuestion();
    }

    /**
     * Step 2: Verify secret answer
     * Returns true if answer is correct
     */
    public boolean verifySecretAnswer(String email, String answer) {
        com.tcs.Library.entity.User user = userDS.findByEmail(email)
                .orElseThrow(() -> new NoUserFoundException("No account found with email: " + email));

        // Compare with hashed answer (case-insensitive, trimmed)
        return passwordEncoder.matches(answer.toLowerCase().trim(), user.getSecretAnswerHash());
    }

    /**
     * Step 3: Reset password after verifying secret answer
     * Returns true if password was reset successfully
     */
    public boolean resetPassword(String email, String secretAnswer, String newPassword) {
        com.tcs.Library.entity.User user = userDS.findByEmail(email)
                .orElseThrow(() -> new NoUserFoundException("No account found with email: " + email));

        // Verify secret answer first
        if (!passwordEncoder.matches(secretAnswer.toLowerCase().trim(), user.getSecretAnswerHash())) {
            throw new IllegalArgumentException("Incorrect secret answer");
        }

        // Validate new password strength
        if (!UserValidations.checkPassword(newPassword)) {
            throw new com.tcs.Library.error.ValidationException(
                    "Password must be at least 8 characters with uppercase, lowercase, number and special character");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userDS.save(user);

        return true;
    }
}
