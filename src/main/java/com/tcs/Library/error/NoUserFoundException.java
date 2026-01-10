package com.tcs.Library.error;

public class NoUserFoundException extends RuntimeException {
 public NoUserFoundException() {
        super();
    }

    public NoUserFoundException(String message) {
        super(message);
    }
}
