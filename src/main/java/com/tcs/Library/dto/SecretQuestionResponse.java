package com.tcs.Library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing the secret question
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecretQuestionResponse {
    private boolean success;
    private String email;
    private String secretQuestion;
    private String message;

    public static SecretQuestionResponse success(String email, String question) {
        return new SecretQuestionResponse(true, email, question, null);
    }

    public static SecretQuestionResponse error(String message) {
        return new SecretQuestionResponse(false, null, null, message);
    }
}
