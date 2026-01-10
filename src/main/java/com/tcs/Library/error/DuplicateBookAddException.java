package com.tcs.Library.error;

public class DuplicateBookAddException extends RuntimeException {
    public DuplicateBookAddException() {
        super();
    }

    public DuplicateBookAddException(String message) {
        super(message);
    }
}
