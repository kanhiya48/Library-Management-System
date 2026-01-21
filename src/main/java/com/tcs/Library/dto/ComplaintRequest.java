package com.tcs.Library.dto;

import com.tcs.Library.enums.ComplaintCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for submitting a new complaint.
 */
@Data
@Getter
@Setter
public class ComplaintRequest {

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject cannot exceed 200 characters")
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private ComplaintCategory category;
}
