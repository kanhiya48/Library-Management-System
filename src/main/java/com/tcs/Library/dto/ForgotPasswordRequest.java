package com.tcs.Library.dto;

import lombok.Data;

/**
 * Request to get secret question by email
 */
@Data
public class ForgotPasswordRequest {
    private String email;
}
