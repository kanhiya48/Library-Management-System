package com.tcs.Library.error;

/**
 * Exception thrown when author email already exists.
 */
public class DuplicateAuthorException extends RuntimeException {

    public DuplicateAuthorException(String email) {
        super("Author with email already exists: " + email);
    }
}
