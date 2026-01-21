package com.tcs.Library.error;

/**
 * Exception thrown for invalid complaint actions.
 */
public class InvalidComplaintActionException extends RuntimeException {

    public InvalidComplaintActionException(String message) {
        super(message);
    }
}
