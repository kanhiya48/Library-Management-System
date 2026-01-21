package com.tcs.Library.error;

/**
 * Exception thrown when an invalid book operation is attempted.
 * Examples: returning a book that's not borrowed, returning a book you didn't
 * borrow.
 */
public class InvalidBookOperationException extends RuntimeException {
    public InvalidBookOperationException(String message) {
        super(message);
    }
}
