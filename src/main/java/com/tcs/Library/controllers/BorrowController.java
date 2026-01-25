package com.tcs.Library.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.IssueBookRequest;
import com.tcs.Library.entity.IssuedBooks;
import com.tcs.Library.entity.User;
import com.tcs.Library.service.BorrowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * Issue a book to a user (Staff/Admin only).
     */
    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<IssuedBooks>> issueBook(@RequestBody IssueBookRequest request) {
        IssuedBooks issued = borrowService.issueBook(request);
        return ResponseEntity.ok(ApiResponse.success("Book issued successfully", issued));
    }

    /**
     * Return a book by copy ID (Staff/Admin only).
     */
    @PostMapping("/return/{bookCopyId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<IssuedBooks>> returnBook(@PathVariable Long bookCopyId) {
        IssuedBooks returned = borrowService.returnBook(bookCopyId);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", returned));
    }

    /**
     * Return a book by issue record ID (Staff/Admin only).
     */
    @PostMapping("/return/record/{recordId}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<IssuedBooks>> returnBookByRecord(@PathVariable Long recordId) {
        IssuedBooks returned = borrowService.returnBookByRecordId(recordId);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", returned));
    }

    /**
     * User returns their own borrowed book using book public ID.
     */
    @PostMapping("/my/return/{bookPublicId}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<IssuedBooks>> returnMyBook(
            @PathVariable String bookPublicId,
            @AuthenticationPrincipal User user) {
        IssuedBooks returned = borrowService.returnBookByUser(user, bookPublicId);
        return ResponseEntity.ok(ApiResponse.success("Return initiated successfully. Pending approval.", returned));
    }

    /**
     * Approve a return request (Admin/Staff only).
     */
    @PostMapping("/return/approve/{recordId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<IssuedBooks>> approveReturn(@PathVariable Long recordId) {
        IssuedBooks returned = borrowService.approveReturn(recordId);
        return ResponseEntity.ok(ApiResponse.success("Return approved and processed successfully", returned));
    }

    /**
     * Get current user's borrowed books.
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<IssuedBooks>>> getMyBorrowedBooks(
            @AuthenticationPrincipal User user) {
        List<IssuedBooks> books = borrowService.getUserBorrowedBooks(user);
        return ResponseEntity.ok(ApiResponse.success("Currently borrowed books", books));
    }

    /**
     * Get current user's borrow history.
     */
    @GetMapping("/my/history")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<IssuedBooks>>> getMyBorrowHistory(
            @AuthenticationPrincipal User user) {
        List<IssuedBooks> history = borrowService.getUserBorrowHistory(user);
        return ResponseEntity.ok(ApiResponse.success("Borrow history", history));
    }

    /**
     * Get current user's overdue books.
     */
    @GetMapping("/my/overdue")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<IssuedBooks>>> getMyOverdueBooks(
            @AuthenticationPrincipal User user) {
        List<IssuedBooks> overdue = borrowService.getUserOverdueBooks(user);
        return ResponseEntity.ok(ApiResponse.success("Overdue books", overdue));
    }
}
