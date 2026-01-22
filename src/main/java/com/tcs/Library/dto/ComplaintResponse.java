package com.tcs.Library.dto;

import java.time.LocalDateTime;

import com.tcs.Library.enums.ComplaintCategory;
import com.tcs.Library.enums.ComplaintStatus;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for returning complaint information to the user.
 */
@Data
@Getter
@Setter
@Builder
public class ComplaintResponse {

    private String complaintId;
    private String subject;
    private String description;
    private ComplaintCategory category;
    private ComplaintStatus status;
    private String complainantName;

    private String assignedStaffName;
    private String secondStaffName;
    private String adminName;

    private String staffResponse;
    private String secondStaffResponse;
    private String adminResponse;

    private String resolutionNotes;
    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime assignedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime updatedAt;
}
