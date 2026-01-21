package com.tcs.Library.dto.wrapper;

import com.tcs.Library.dto.ComplaintResponse;
import com.tcs.Library.entity.Complaint;

/**
 * Mapper to convert Complaint entity to ComplaintResponse DTO.
 */
public class ComplaintMapper {

    private ComplaintMapper() {
    }

    public static ComplaintResponse toResponse(Complaint complaint) {
        if (complaint == null) {
            return null;
        }

        return ComplaintResponse.builder()
                .complaintId(complaint.getComplaintId())
                .subject(complaint.getSubject())
                .description(complaint.getDescription())
                .category(complaint.getCategory())
                .status(complaint.getStatus())
                .assignedStaffName(complaint.getAssignedStaff() != null
                        ? complaint.getAssignedStaff().getCustomerName()
                        : null)
                .secondStaffName(complaint.getSecondAssignedStaff() != null
                        ? complaint.getSecondAssignedStaff().getCustomerName()
                        : null)
                .adminName(complaint.getAssignedAdmin() != null
                        ? complaint.getAssignedAdmin().getCustomerName()
                        : null)
                .staffResponse(complaint.getStaffResponse())
                .secondStaffResponse(complaint.getSecondStaffResponse())
                .adminResponse(complaint.getAdminResponse())
                .resolutionNotes(complaint.getResolutionNotes())
                .rejectionReason(complaint.getRejectionReason())
                .createdAt(complaint.getCreatedAt())
                .assignedAt(complaint.getAssignedAt())
                .resolvedAt(complaint.getResolvedAt())
                .updatedAt(complaint.getUpdatedAt())
                .build();
    }
}
