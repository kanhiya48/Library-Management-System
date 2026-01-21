package com.tcs.Library.enums;

/**
 * Enum representing the lifecycle status of a complaint.
 */
public enum ComplaintStatus {
    /** Complaint has been submitted but not yet assigned */
    PENDING,

    /** Complaint has been assigned to staff for review */
    ASSIGNED,

    /** Complaint is being actively worked on */
    IN_PROGRESS,

    /** Complaint has been resolved successfully */
    RESOLVED,

    /** Complaint rejected by first staff member */
    REJECTED_FIRST,

    /** Complaint rejected by both staff members, escalated to admin */
    ESCALATED_TO_ADMIN,

    /** Complaint rejected by admin (final decision) */
    REJECTED,

    /** Complaint closed after resolution or admin action */
    CLOSED
}
