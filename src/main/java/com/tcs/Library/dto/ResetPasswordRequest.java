package com.tcs.Library.dto;

import lombok.Data;

/**
 * Request to reset password
 */
@Data
public class ResetPasswordRequest {
    private String email;
    private String secretAnswer;
    private String newPassword;
    private String confirmNewPassword;
}
