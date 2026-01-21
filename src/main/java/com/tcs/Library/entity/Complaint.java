package com.tcs.Library.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tcs.Library.enums.ComplaintCategory;
import com.tcs.Library.enums.ComplaintStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entity representing a user complaint in the library system.
 * Tracks complaints from submission through resolution or rejection.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "complaint_id", nullable = false, unique = true, updatable = false)
    private String complaintId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User complainant;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    /** First staff member assigned to handle the complaint */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private User assignedStaff;

    /** Second staff member assigned after first rejection */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_staff_id")
    private User secondAssignedStaff;

    /** Admin assigned after both staff rejections */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User assignedAdmin;

    @Column(length = 1000)
    private String staffResponse;

    @Column(length = 1000)
    private String secondStaffResponse;

    @Column(length = 1000)
    private String adminResponse;

    @Column(length = 500)
    private String rejectionReason;

    @Column(length = 500)
    private String resolutionNotes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime assignedAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime updatedAt;

    /** Whether the first staff member has rejected */
    @Column(nullable = false)
    private boolean firstStaffRejected = false;

    /** Whether the second staff member has rejected */
    @Column(nullable = false)
    private boolean secondStaffRejected = false;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        if (complaintId == null) {
            complaintId = "CMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
