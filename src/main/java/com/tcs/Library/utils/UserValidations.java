
package com.tcs.Library.utils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;

import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

import lombok.*;
import com.tcs.Library.dto.*;

import java.security.SecureRandom;
import com.tcs.Library.service.*;

@Component
@RequiredArgsConstructor
public class UserValidations {

    // -----------------------------
    // Error messages (as provided)
    // -----------------------------
    public static final String ERR_NAME = "Name must be at least 3 characters long and contain only letters.";
    public static final String ERR_EMAIL = "Enter a valid email address.";
    public static final String ERR_EMAIL_DUP = "Email already registered";
    public static final String ERR_MOBILE = "Enter a valid mobile number.";
    public static final String ERR_MOBILE_DUP = "Mobile number already registered.";
    public static final String ERR_ADDRESS = "Address must be at least 10 characters long.";
    public static final String ERR_DOB_AGE = "member must be 18 years old to create account.";
    public static final String ERR_PASSWORD = "Password must be at least 8 characters and include a mix of uppercase, lowercase, number, and special character.";
    public static final String ERR_CONFIRM_PASSWORD = "Passwords do not match.";
    public static final String ERR_SECRET_Q = "Secret question is required.";
    public static final String ERR_SECRET_A = "Secret answer is required.";
    public static final String ERR_COUNTRY_CODE = "Country code is required.";

    // -----------------------------
    // Regex Patterns
    // -----------------------------
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z ]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DIGITS_ONLY = Pattern.compile("^\\d+$");
    private static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile("^\\+[1-9]\\d{0,3}$"); // e.g.
                                                                                               // +91,
                                                                                               // +1,
                                                                                               // +971

    // Password requires:
    // - min 8
    // - at least 1 uppercase
    // - at least 1 lowercase
    // - at least 1 digit
    // - at least 1 special
    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    // -----------------------------
    // Public Validation Methods
    // -----------------------------
    private final UniqueCheckService uniqueCheckService;

    public ValidationResult validateRegistration(UserRegRequest req, Clock clock) {
        ValidationResult result = new ValidationResult();

        // 1. Name
        if (!checkName(req.getCustomerName())) {
            result.addError("customerName", ERR_NAME);
        }

        // 2. Email (format + unique)
        if (!checkEmail(req.getEmail())) {
            result.addError("email", ERR_EMAIL);
        } else if (this.uniqueCheckService != null
                && this.uniqueCheckService.isEmailRegistered(req.getEmail())) {
            result.addError("email", ERR_EMAIL_DUP);
        }

        // 3. Mobile (country code mandatory + digits + length + unique)
        if (!checkCountryCode(req.getCountryCode())) {
            result.addError("countryCode", ERR_COUNTRY_CODE);
        }

        if (!checkMobile(req.getMobileNumber())) {
            result.addError("mobileNumber", ERR_MOBILE);
        } else if (uniqueCheckService != null && uniqueCheckService
                .isMobileRegistered(req.getCountryCode(), req.getMobileNumber())) {
            result.addError("mobileNumber", ERR_MOBILE_DUP);
        }

        // 4. Address (single string OR composite)
        boolean addressOk;
        if (req.getAddress() != null) {
            addressOk = checkAddress(req.getAddress());
        } else {
            // composite address validation
            addressOk = checkCompositeAddress(req.getAddressLine1(), req.getAddressLine2(),
                    req.getCity(), req.getState(), req.getPinCode());
        }
        if (!addressOk) {
            result.addError("address", ERR_ADDRESS);
        }

        // 5. DOB (required + age >= 14)
        if (!checkDob(req.getDateOfBirth(), clock)) {
            result.addError("dateOfBirth", ERR_DOB_AGE);
        }

        // 6. Password
        if (!checkPassword(req.getPassword())) {
            result.addError("password", ERR_PASSWORD);
        }

        // 7. Confirm Password
        if (!checkConfirmPassword(req.getPassword(), req.getConfirmPassword())) {
            result.addError("confirmPassword", ERR_CONFIRM_PASSWORD);
        }

        // 8. Secret Question + Answer
        if (isBlank(req.getSecretQuestion())) {
            result.addError("secretQuestion", ERR_SECRET_Q);
        }
        if (isBlank(req.getSecretAnswer())) {
            result.addError("secretAnswer", ERR_SECRET_A);
        }

        return result;
    }

    // 1) Name: required, min 3, alphabets & spaces only
    public static boolean checkName(String name) {
        if (isBlank(name))
            return false;
        String trimmed = name.trim();
        if (trimmed.length() < 3)
            return false;
        return NAME_PATTERN.matcher(trimmed).matches();
    }

    // 2) Email: required, valid email format
    public static boolean checkEmail(String email) {
        if (isBlank(email))
            return false;
        String trimmed = email.trim();
        return EMAIL_PATTERN.matcher(trimmed).matches();
    }

    // Country Code: required dropdown
    public static boolean checkCountryCode(String countryCode) {
        if (isBlank(countryCode))
            return false;
        return COUNTRY_CODE_PATTERN.matcher(countryCode.trim()).matches();
    }

    // 3) Mobile: required, digits only, min 8 max 10
    public static boolean checkMobile(String mobile) {
        if (isBlank(mobile))
            return false;
        String trimmed = mobile.trim();
        if (!DIGITS_ONLY.matcher(trimmed).matches())
            return false;
        int len = trimmed.length();
        return len >= 8 && len <= 10;
    }

    // 4) Address: required min 10
    public static boolean checkAddress(String address) {
        if (isBlank(address))
            return false;
        return address.trim().length() >= 10;
    }

    // Composite address option (you can tune this further)
    public static boolean checkCompositeAddress(String line1, String line2, String city,
            String state, String pinCode) {
        // at minimum require line1, city, state, pin; and total length >= 10
        if (isBlank(line1) || isBlank(city) || isBlank(state) || isBlank(pinCode))
            return false;

        String combined = String
                .join(" ", safe(line1), safe(line2), safe(city), safe(state), safe(pinCode)).trim();

        if (combined.length() < 10)
            return false;

        // Pin code digits check (India example 6 digits; adjust if needed)
        String pc = pinCode.trim();
        if (!DIGITS_ONLY.matcher(pc).matches())
            return false;
        if (pc.length() < 4 || pc.length() > 10)
            return false; // generic range

        return true;
    }

    // 5) DOB: required, must be at least 14 years old.
    // "No spaces" is naturally satisfied by LocalDate; if you store as string, trim
    // it before
    // parsing.
    public static boolean checkDob(LocalDate dob, Clock clock) {
        if (dob == null)
            return false;

        LocalDate today = LocalDate.now(clock == null ? Clock.systemDefaultZone() : clock);

        // can't be future
        if (dob.isAfter(today))
            return false;

        int age = Period.between(dob, today).getYears();
        return age >= 14;
    }

    /**
     * Helper for UI: Max selectable DOB so user is at least 14. If today =
     * 2025-01-01 => max DOB =
     * 2011-01-01 (as you requested).
     */
    public static LocalDate getMaxSelectableDobFor14Years(Clock clock) {
        LocalDate today = LocalDate.now(clock == null ? Clock.systemDefaultZone() : clock);
        return today.minusYears(14);
    }

    // 6) Password: required, strong pattern
    public static boolean checkPassword(String password) {
        if (isBlank(password))
            return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    // 7) Confirm password matches
    public static boolean checkConfirmPassword(String password, String confirmPassword) {
        if (isBlank(password) || isBlank(confirmPassword))
            return false;
        return Objects.equals(password, confirmPassword);
    }

    // -----------------------------
    // Password Hashing (PBKDF2)
    // -----------------------------
    private static final int PBKDF2_ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BYTES = 32; // 256-bit

    /**
     * Hash password using PBKDF2WithHmacSHA256. Returns:
     * iterations:saltBase64:hashBase64
     */
    public static String hashPassword(String rawPassword) {
        if (isBlank(rawPassword)) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);

        byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES);

        return PBKDF2_ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String rawPassword, String stored) {
        if (isBlank(rawPassword) || isBlank(stored))
            return false;

        String[] parts = stored.split(":");
        if (parts.length != 3)
            return false;

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[2]);

        byte[] computedHash = pbkdf2(rawPassword.toCharArray(), salt, iterations, expectedHash.length);

        return constantTimeEquals(expectedHash, computedHash);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int hashBytes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, hashBytes * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null)
            return false;
        if (a.length != b.length)
            return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= (a[i] ^ b[i]);
        }
        return result == 0;
    }

    // -----------------------------
    // Helpers
    // -----------------------------
    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // -----------------------------
    // Result & Request Models
    // -----------------------------

    public static class ValidationResult {
        private final List<FieldError> errors = new ArrayList<>();

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<FieldError> getErrors() {
            return errors;
        }

        public void addError(String field, String message) {
            errors.add(new FieldError(field, message));
        }
    }

    @RequiredArgsConstructor
    public static class FieldError {
        @Getter
        private final String field;
        private final String message;
    }
}
