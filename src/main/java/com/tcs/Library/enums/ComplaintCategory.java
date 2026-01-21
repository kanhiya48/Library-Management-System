package com.tcs.Library.enums;

/**
 * Categories of complaints that users can submit.
 */
public enum ComplaintCategory {
    /** Issues related to book condition or quality */
    BOOK_DAMAGE,

    /** Complaints about wrong fines or penalties */
    FINE_DISPUTE,

    /** Issues with library staff behavior */
    STAFF_BEHAVIOR,

    /** Problems with borrowing or returning books */
    BORROW_ISSUE,

    /** Technical problems with the library system */
    SYSTEM_ERROR,

    /** Issues related to library facilities */
    FACILITY,

    /** Issues related to donation process */
    DONATION_ISSUE,

    /** Any other category not listed */
    OTHER
}
