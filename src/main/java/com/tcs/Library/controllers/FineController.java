package com.tcs.Library.controllers;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.FineResponse;
import com.tcs.Library.entity.Fine;
import com.tcs.Library.entity.IssuedBooks;
import com.tcs.Library.entity.User;
import com.tcs.Library.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FineResponse>>> getMyFines(@AuthenticationPrincipal User user) {
        List<Fine> fines = fineService.getUserFines(user.getId());
        List<FineResponse> response = fines.stream()
                .map(this::mapToFineResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Fines retrieved", response));
    }

    @GetMapping("/unpaid")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<FineResponse>>> getUnpaidFines(@AuthenticationPrincipal User user) {
        List<Fine> fines = fineService.getUnpaidFines(user.getId());
        List<FineResponse> response = fines.stream()
                .map(this::mapToFineResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Unpaid fines retrieved", response));
    }

    @PostMapping("/{fineId}/pay")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<FineResponse>> payFine(@PathVariable Long fineId) {
        Fine paid = fineService.payFine(fineId);
        return ResponseEntity.ok(ApiResponse.success("Fine paid successfully", mapToFineResponse(paid)));
    }

    private FineResponse mapToFineResponse(Fine fine) {
        FineResponse response = new FineResponse();
        response.setId(fine.getId());
        response.setAmount(fine.getAmount());
        response.setPaid(fine.isPaid());
        response.setCreatedAt(fine.getCreatedAt());
        response.setPaidAt(fine.getPaidAt());

        IssuedBooks issuedBook = fine.getIssuedBook();
        if (issuedBook != null) {
            response.setIssuedDate(issuedBook.getIssueDate());
            response.setDueDate(issuedBook.getDueDate());
            if (issuedBook.getBookCopy() != null && issuedBook.getBookCopy().getBook() != null) {
                response.setBookTitle(issuedBook.getBookCopy().getBook().getBookTitle());
            }
        }
        return response;
    }
}
