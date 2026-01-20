package com.tcs.Library.dto;

import lombok.Data;

/**
 * Request to verify secret answer
 */
@Data
public class VerifyAnswerRequest {
    private String email;
    private String secretAnswer;
}
