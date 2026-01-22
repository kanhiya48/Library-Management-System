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
 * Controller for admin to manage escalated and all complaints.
 */
@RestController
@RequestMapping("/admin/complaints")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminComplaintController {

    private final ComplaintService complaintService;

    /**
     * Get all complaints (admin view).
     */
    /**
     * Get all complaints (admin view).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ComplaintResponse>>> getAllComplaints(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ComplaintResponse> complaints = complaintService.getAllComplaints(searchTerm, category, status, startDate,
                endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success("All complaints retrieved", PagedResponse.from(complaints)));
    }

    /**
     * Get escalated complaints only.
     */
    @GetMapping("/escalated")
    public ResponseEntity<ApiResponse<PagedResponse<ComplaintResponse>>> getEscalatedComplaints(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ComplaintResponse> complaints = complaintService.getAdminComplaints(searchTerm, category, status,
                startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success("Escalated complaints retrieved", PagedResponse.from(complaints)));
    }

    /**
     * Get details of a specific complaint.
     */
    @GetMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaint(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User admin) {
        ComplaintResponse response = complaintService.getComplaint(complaintId, admin);
        return ResponseEntity.ok(ApiResponse.success("Complaint details", response));
    }

    /**
     * Admin resolve an escalated complaint.
     */
    @PostMapping("/{complaintId}/resolve")
    public ResponseEntity<ApiResponse<ComplaintResponse>> resolveComplaint(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User admin,
            @Valid @RequestBody ComplaintActionRequest request) {
        request.setAction("RESOLVE");
        ComplaintResponse response = complaintService.adminAction(complaintId, admin, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint resolved by admin", response));
    }

    /**
     * Admin reject an escalated complaint (final decision).
     */
    @PostMapping("/{complaintId}/reject")
    public ResponseEntity<ApiResponse<ComplaintResponse>> rejectComplaint(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User admin,
            @Valid @RequestBody ComplaintActionRequest request) {
        request.setAction("REJECT");
        ComplaintResponse response = complaintService.adminAction(complaintId, admin, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint rejected by admin", response));
    }
}
