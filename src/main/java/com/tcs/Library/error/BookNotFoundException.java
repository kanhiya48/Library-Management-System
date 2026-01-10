package com.tcs.Library.error;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException() {
        super();
    }

    public BookNotFoundException(String message) {
        super(message);
    }
}
