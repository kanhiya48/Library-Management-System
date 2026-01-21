package com.tcs.Library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for staff/admin to resolve or reject a complaint.
 */
@Data
@Getter
@Setter
public class ComplaintActionRequest {

    /** Action: "RESOLVE" or "REJECT" */
    @NotBlank(message = "Action is required")
    private String action;

    @Size(max = 1000, message = "Response cannot exceed 1000 characters")
    private String response;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
