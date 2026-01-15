package com.tcs.Library.error;

public class MaxBooksExceededException extends RuntimeException {
    public MaxBooksExceededException(String message) {
        super(message);
    }
}
