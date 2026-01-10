
package com.tcs.Library.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect Credentials");
    }

    @ExceptionHandler(NoAuthorFoundException.class)
    public ResponseEntity<String> handleNoauthorFound(NoAuthorFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("No Author Found" + ex.getMessage());
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> BookNotFoundException(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("No Book Found with this Public Id" + ex.getMessage());
    }

    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<String> NoUserFoundException(NoUserFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("No User Found with this Id" + ex.getMessage());
    }

    @ExceptionHandler(DuplicateBookAddException.class)
    public ResponseEntity<String> DuplicateBookAddException(DuplicateBookAddException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("No User Found with this Id" + ex.getMessage());
    }


}
