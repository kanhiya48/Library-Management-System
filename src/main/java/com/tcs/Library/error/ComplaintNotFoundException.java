package com.tcs.Library.error;

/**
 * Exception thrown when a complaint is not found.
 */
public class ComplaintNotFoundException extends RuntimeException {

    public ComplaintNotFoundException(String complaintId) {
        super("Complaint not found: " + complaintId);
    }
}
