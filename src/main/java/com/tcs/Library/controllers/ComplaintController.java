package com.tcs.Library.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.PagedResponse;
import com.tcs.Library.dto.ComplaintRequest;
import com.tcs.Library.dto.ComplaintResponse;
import com.tcs.Library.entity.User;
import com.tcs.Library.service.ComplaintService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller for handling user complaints.
 */
@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    /**
     * Submit a new complaint.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ComplaintResponse>> submitComplaint(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ComplaintRequest request) {
        ComplaintResponse response = complaintService.submitComplaint(user, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint submitted successfully", response));
    }

    /**
     * Get all complaints for the current user with pagination.
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PagedResponse<ComplaintResponse>>> getMyComplaints(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ComplaintResponse> complaints = complaintService.getUserComplaints(user, pageable);
        return ResponseEntity.ok(ApiResponse.success("Complaints retrieved", PagedResponse.from(complaints)));
    }

    /**
     * Get a specific complaint by ID.
     */
    @GetMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaint(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User user) {
        ComplaintResponse response = complaintService.getComplaint(complaintId, user);
        return ResponseEntity.ok(ApiResponse.success("Complaint retrieved", response));
    }

    /**
     * Track complaint status.
     */
    @GetMapping("/{complaintId}/track")
    public ResponseEntity<ApiResponse<ComplaintResponse>> trackComplaint(
            @PathVariable String complaintId,
            @AuthenticationPrincipal User user) {
        ComplaintResponse response = complaintService.getComplaint(complaintId, user);
        return ResponseEntity.ok(ApiResponse.success("Complaint status", response));
    }
}
