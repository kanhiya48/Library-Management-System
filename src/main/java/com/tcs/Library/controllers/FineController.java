package com.tcs.Library.controllers;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.entity.Fine;
import com.tcs.Library.entity.User;
import com.tcs.Library.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Fine>>> getMyFines(@AuthenticationPrincipal User user) {
        List<Fine> fines = fineService.getUserFines(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Fines retrieved", fines));
    }

    @GetMapping("/unpaid")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Fine>>> getUnpaidFines(@AuthenticationPrincipal User user) {
        List<Fine> fines = fineService.getUnpaidFines(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Unpaid fines retrieved", fines));
    }

    @PostMapping("/{fineId}/pay")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Fine>> payFine(@PathVariable Long fineId) {
        Fine paid = fineService.payFine(fineId);
        return ResponseEntity.ok(ApiResponse.success("Fine paid successfully", paid));
    }
}
