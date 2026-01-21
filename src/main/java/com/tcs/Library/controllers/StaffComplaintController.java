package com.tcs.Library.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.PagedResponse;
import com.tcs.Library.dto.ComplaintActionRequest;
import com.tcs.Library.dto.ComplaintResponse;
import com.tcs.Library.entity.User;
import com.tcs.Library.service.ComplaintService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for staff to manage assigned complaints.
 */
@RestController
@RequestMapping("/staff/complaints")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class StaffComplaintController {

    private final ComplaintService complaintService;

    /**
     * Get all complaints assigned to the current staff member.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ComplaintResponse>>> getAssignedComplaints(
            @AuthenticationPrincipal User staff,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ComplaintResponse> complaints = complaintService.getStaffComplaints(staff, pageable);
        return ResponseEntity.ok(ApiResponse.success("Assigned complaints retrieved", PagedResponse.from(complaints)));
    }

    /**
     * Get details of a specific complaint.
     */
    @GetMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaint(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User staff) {
        ComplaintResponse response = complaintService.getComplaint(complaintId, staff);
        return ResponseEntity.ok(ApiResponse.success("Complaint details", response));
    }

    /**
     * Mark a complaint as in progress.
     */
    @PostMapping("/{complaintId}/in-progress")
    public ResponseEntity<ApiResponse<ComplaintResponse>> markInProgress(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User staff) {
        ComplaintResponse response = complaintService.markInProgress(complaintId, staff);
        return ResponseEntity.ok(ApiResponse.success("Complaint marked as in progress", response));
    }

    /**
     * Resolve a complaint.
     */
    @PostMapping("/{complaintId}/resolve")
    public ResponseEntity<ApiResponse<ComplaintResponse>> resolveComplaint(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User staff,
            @Valid @RequestBody ComplaintActionRequest request) {
        request.setAction("RESOLVE");
        ComplaintResponse response = complaintService.staffAction(complaintId, staff, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint resolved", response));
    }

    /**
     * Reject a complaint (escalates to second staff or admin).
     */
    @PostMapping("/{complaintId}/reject")
    public ResponseEntity<ApiResponse<ComplaintResponse>> rejectComplaint(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User staff,
            @Valid @RequestBody ComplaintActionRequest request) {
        request.setAction("REJECT");
        ComplaintResponse response = complaintService.staffAction(complaintId, staff, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint rejected", response));
    }
}
