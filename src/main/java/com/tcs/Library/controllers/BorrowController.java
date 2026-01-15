package com.tcs.Library.controllers;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.IssueBookRequest;
import com.tcs.Library.entity.IssuedBooks;
import com.tcs.Library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<IssuedBooks>> issueBook(@RequestBody IssueBookRequest request) {
        IssuedBooks issued = borrowService.issueBook(request);
        return ResponseEntity.ok(ApiResponse.success("Book issued successfully", issued));
    }

    @PostMapping("/return/{bookCopyId}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<IssuedBooks>> returnBook(@PathVariable Long bookCopyId) {
        IssuedBooks returned = borrowService.returnBook(bookCopyId);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", returned));
    }
}
