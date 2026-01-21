package com.tcs.Library.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.Library.dto.ComplaintActionRequest;
import com.tcs.Library.dto.ComplaintRequest;
import com.tcs.Library.dto.ComplaintResponse;
import com.tcs.Library.dto.wrapper.ComplaintMapper;
import com.tcs.Library.entity.Complaint;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.ComplaintStatus;
import com.tcs.Library.enums.Role;
import com.tcs.Library.error.ComplaintNotFoundException;
import com.tcs.Library.error.InvalidComplaintActionException;
import com.tcs.Library.repository.ComplaintRepo;
import com.tcs.Library.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {

    private final ComplaintRepo complaintRepo;
    private final UserRepo userRepo;

    private static final List<ComplaintStatus> ACTIVE_STATUSES = Arrays.asList(
            ComplaintStatus.PENDING,
            ComplaintStatus.ASSIGNED,
            ComplaintStatus.IN_PROGRESS);

    /**
     * Submit a new complaint from a user.
     */
    @Transactional
    public ComplaintResponse submitComplaint(User user, ComplaintRequest request) {
        Complaint complaint = new Complaint();
        complaint.setComplainant(user);
        complaint.setSubject(request.getSubject());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setStatus(ComplaintStatus.PENDING);

        Complaint saved = complaintRepo.save(complaint);

        // Auto-assign to staff
        autoAssignToStaff(saved);

        return ComplaintMapper.toResponse(saved);
    }

    /**
     * Auto-assign a complaint to a staff member with the least active complaints.
     */
    @Transactional
    public void autoAssignToStaff(Complaint complaint) {
        List<User> staffMembers = userRepo.findAll().stream()
                .filter(u -> u.getRoles().contains(Role.STAFF))
                .toList();

        if (staffMembers.isEmpty()) {
            log.warn("No staff members available for auto-assignment of complaint: {}",
                    complaint.getComplaintId());
            return;
        }

        // Find staff with least active complaints
        User selectedStaff = staffMembers.stream()
                .min((s1, s2) -> {
                    long count1 = complaintRepo.countActiveComplaintsByStaff(s1, ACTIVE_STATUSES);
                    long count2 = complaintRepo.countActiveComplaintsByStaff(s2, ACTIVE_STATUSES);
                    return Long.compare(count1, count2);
                })
                .orElse(staffMembers.get(0));

        complaint.setAssignedStaff(selectedStaff);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaint.setAssignedAt(LocalDateTime.now());
        complaintRepo.save(complaint);

        log.info("Complaint {} auto-assigned to staff: {}",
                complaint.getComplaintId(), selectedStaff.getEmail());
    }

    /**
     * Get all complaints for a user.
     */
    public Page<ComplaintResponse> getUserComplaints(User user, Pageable pageable) {
        return complaintRepo.findByComplainant(user, pageable)
                .map(ComplaintMapper::toResponse);
    }

    /**
     * Get a specific complaint by ID for a user.
     */
    public ComplaintResponse getComplaint(String complaintId, User user) {
        Complaint complaint = complaintRepo.findByComplaintId(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException(complaintId));

        // Check if user is the complainant or has staff/admin access
        boolean isOwner = complaint.getComplainant().getId().equals(user.getId());
        boolean isStaff = user.getRoles().contains(Role.STAFF) || user.getRoles().contains(Role.ADMIN);

        if (!isOwner && !isStaff) {
            throw new InvalidComplaintActionException("You don't have access to this complaint");
        }

        return ComplaintMapper.toResponse(complaint);
    }

    /**
     * Get complaints assigned to a staff member.
     */
    public Page<ComplaintResponse> getStaffComplaints(User staff, Pageable pageable) {
        return complaintRepo.findByAssignedStaff(staff, pageable)
                .map(ComplaintMapper::toResponse);
    }

    /**
     * Get all pending and escalated complaints for admin.
     */
    public Page<ComplaintResponse> getAdminComplaints(Pageable pageable) {
        List<ComplaintStatus> adminStatuses = Arrays.asList(
                ComplaintStatus.PENDING,
                ComplaintStatus.ESCALATED_TO_ADMIN);
        return complaintRepo.findByStatusIn(adminStatuses, pageable)
                .map(ComplaintMapper::toResponse);
    }

    /**
     * Staff action on a complaint (resolve or reject).
     */
    @Transactional
    public ComplaintResponse staffAction(String complaintId, User staff, ComplaintActionRequest request) {
        Complaint complaint = complaintRepo.findByComplaintId(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException(complaintId));

        // Verify staff is assigned to this complaint
        boolean isAssignedStaff = (complaint.getAssignedStaff() != null &&
                complaint.getAssignedStaff().getId().equals(staff.getId()));
        boolean isSecondStaff = (complaint.getSecondAssignedStaff() != null &&
                complaint.getSecondAssignedStaff().getId().equals(staff.getId()));

        if (!isAssignedStaff && !isSecondStaff) {
            throw new InvalidComplaintActionException("You are not assigned to this complaint");
        }

        String action = request.getAction().toUpperCase();

        if ("RESOLVE".equals(action)) {
            return resolveByStaff(complaint, staff, request, isSecondStaff);
        } else if ("REJECT".equals(action)) {
            return rejectByStaff(complaint, staff, request, isSecondStaff);
        } else {
            throw new InvalidComplaintActionException("Invalid action. Use RESOLVE or REJECT");
        }
    }

    private ComplaintResponse resolveByStaff(Complaint complaint, User staff,
            ComplaintActionRequest request, boolean isSecondStaff) {
        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setResolvedAt(LocalDateTime.now());
        complaint.setResolutionNotes(request.getNotes());

        if (isSecondStaff) {
            complaint.setSecondStaffResponse(request.getResponse());
        } else {
            complaint.setStaffResponse(request.getResponse());
        }

        Complaint saved = complaintRepo.save(complaint);
        log.info("Complaint {} resolved by staff: {}", complaintId(complaint), staff.getEmail());
        return ComplaintMapper.toResponse(saved);
    }

    private ComplaintResponse rejectByStaff(Complaint complaint, User staff,
            ComplaintActionRequest request, boolean isSecondStaff) {

        if (isSecondStaff) {
            // Second staff rejecting - escalate to admin
            complaint.setSecondStaffRejected(true);
            complaint.setSecondStaffResponse(request.getResponse());
            complaint.setStatus(ComplaintStatus.ESCALATED_TO_ADMIN);
            complaint.setRejectionReason(request.getNotes());

            // Auto-assign to an admin
            autoAssignToAdmin(complaint);

            log.info("Complaint {} escalated to admin after second staff rejection",
                    complaintId(complaint));
        } else {
            // First staff rejecting - assign to another staff
            complaint.setFirstStaffRejected(true);
            complaint.setStaffResponse(request.getResponse());
            complaint.setStatus(ComplaintStatus.REJECTED_FIRST);

            // Find another staff member
            assignToSecondStaff(complaint, staff);
        }

        Complaint saved = complaintRepo.save(complaint);
        return ComplaintMapper.toResponse(saved);
    }

    /**
     * Assign to a second staff member after first rejection.
     */
    private void assignToSecondStaff(Complaint complaint, User firstStaff) {
        List<User> staffMembers = userRepo.findAll().stream()
                .filter(u -> u.getRoles().contains(Role.STAFF))
                .filter(u -> !u.getId().equals(firstStaff.getId())) // Exclude first staff
                .toList();

        if (staffMembers.isEmpty()) {
            // No other staff available, escalate to admin
            complaint.setStatus(ComplaintStatus.ESCALATED_TO_ADMIN);
            autoAssignToAdmin(complaint);
            log.warn("No second staff available, escalating complaint {} to admin",
                    complaintId(complaint));
            return;
        }

        // Find staff with least active complaints
        User secondStaff = staffMembers.stream()
                .min((s1, s2) -> {
                    long count1 = complaintRepo.countActiveComplaintsByStaff(s1, ACTIVE_STATUSES);
                    long count2 = complaintRepo.countActiveComplaintsByStaff(s2, ACTIVE_STATUSES);
                    return Long.compare(count1, count2);
                })
                .orElse(staffMembers.get(0));

        complaint.setSecondAssignedStaff(secondStaff);
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        log.info("Complaint {} assigned to second staff: {}",
                complaintId(complaint), secondStaff.getEmail());
    }

    /**
     * Auto-assign an escalated complaint to an admin.
     */
    private void autoAssignToAdmin(Complaint complaint) {
        Optional<User> admin = userRepo.findAll().stream()
                .filter(u -> u.getRoles().contains(Role.ADMIN))
                .findFirst();

        admin.ifPresent(a -> {
            complaint.setAssignedAdmin(a);
            log.info("Complaint {} assigned to admin: {}", complaintId(complaint), a.getEmail());
        });

        if (admin.isEmpty()) {
            log.error("No admin available for escalated complaint: {}", complaintId(complaint));
        }
    }

    /**
     * Admin action on an escalated complaint.
     */
    @Transactional
    public ComplaintResponse adminAction(String complaintId, User admin, ComplaintActionRequest request) {
        Complaint complaint = complaintRepo.findByComplaintId(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException(complaintId));

        if (complaint.getStatus() != ComplaintStatus.ESCALATED_TO_ADMIN) {
            throw new InvalidComplaintActionException("Complaint is not escalated to admin");
        }

        String action = request.getAction().toUpperCase();

        if ("RESOLVE".equals(action)) {
            complaint.setStatus(ComplaintStatus.RESOLVED);
            complaint.setResolvedAt(LocalDateTime.now());
            complaint.setAdminResponse(request.getResponse());
            complaint.setResolutionNotes(request.getNotes());
            log.info("Complaint {} resolved by admin: {}", complaintId(complaint), admin.getEmail());
        } else if ("REJECT".equals(action)) {
            complaint.setStatus(ComplaintStatus.REJECTED);
            complaint.setAdminResponse(request.getResponse());
            complaint.setRejectionReason(request.getNotes());
            log.info("Complaint {} rejected by admin: {}", complaintId(complaint), admin.getEmail());
        } else {
            throw new InvalidComplaintActionException("Invalid action. Use RESOLVE or REJECT");
        }

        Complaint saved = complaintRepo.save(complaint);
        return ComplaintMapper.toResponse(saved);
    }

    /**
     * Get all complaints (admin only).
     */
    public Page<ComplaintResponse> getAllComplaints(Pageable pageable) {
        return complaintRepo.findAll(pageable).map(ComplaintMapper::toResponse);
    }

    /**
     * Mark complaint as in progress.
     */
    @Transactional
    public ComplaintResponse markInProgress(String complaintId, User staff) {
        Complaint complaint = complaintRepo.findByComplaintId(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException(complaintId));

        boolean isAssigned = (complaint.getAssignedStaff() != null &&
                complaint.getAssignedStaff().getId().equals(staff.getId())) ||
                (complaint.getSecondAssignedStaff() != null &&
                        complaint.getSecondAssignedStaff().getId().equals(staff.getId()));

        if (!isAssigned) {
            throw new InvalidComplaintActionException("You are not assigned to this complaint");
        }

        complaint.setStatus(ComplaintStatus.IN_PROGRESS);
        Complaint saved = complaintRepo.save(complaint);
        return ComplaintMapper.toResponse(saved);
    }

    private String complaintId(Complaint complaint) {
        return complaint.getComplaintId();
    }
}
