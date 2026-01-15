package com.tcs.Library.error;

import com.tcs.Library.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Incorrect credentials"));
    }

    @ExceptionHandler(NoAuthorFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoAuthorFound(NoAuthorFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Author not found: " + ex.getMessage()));
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleBookNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Book not found: " + ex.getMessage()));
    }

    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoUserFound(NoUserFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("User not found: " + ex.getMessage()));
    }

    @ExceptionHandler(DuplicateBookAddException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateBook(DuplicateBookAddException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Duplicate book: " + ex.getMessage()));
    }

    @ExceptionHandler(UserIsDefaulterException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserIsDefaulter(UserIsDefaulterException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateBookBorrowException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateBorrow(DuplicateBookBorrowException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(NoCopyAvailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoCopyAvailable(NoCopyAvailableException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MaxBooksExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxBooksExceeded(MaxBooksExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Resource was modified by another user. Please retry."));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + ex.getMessage()));
    }
}
