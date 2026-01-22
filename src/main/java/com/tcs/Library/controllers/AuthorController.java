package com.tcs.Library.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.PagedResponse;
import com.tcs.Library.dto.AuthorSignUp;
import com.tcs.Library.entity.Author;
import com.tcs.Library.service.AuthorService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/authors")
@RequiredArgsConstructor
@RestController
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Register a new author.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Author>> register(@RequestBody AuthorSignUp dto) {
        Author author = authorService.registerAuthor(dto);
        return ResponseEntity.ok(ApiResponse.success("Author registered successfully", author));
    }

    /**
     * Get all authors with pagination.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Author>>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Author> authors = authorService.getAllAuthors(pageable);
        return ResponseEntity.ok(ApiResponse.success("Authors retrieved", PagedResponse.from(authors)));
    }

    /**
     * Search authors by name with pagination.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<Author>>> searchAuthors(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Author> authors = authorService.searchByName(name, pageable);
        return ResponseEntity.ok(ApiResponse.success("Authors found", PagedResponse.from(authors)));
    }

    /**
     * Get author by email.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<Author>> getByEmail(@PathVariable String email) {
        Author author = authorService.getByEmail(email);
        return ResponseEntity.ok(ApiResponse.success("Author found", author));
    }

    /**
     * Get author by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Author>> getById(@PathVariable Long id) {
        Author author = authorService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Author found", author));
    }

    /**
     * Check if author email exists.
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = authorService.emailExists(email);
        return ResponseEntity.ok(ApiResponse.success("Email check result", exists));
    }

    /**
     * Update an existing author.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Author>> updateAuthor(
            @PathVariable Long id,
            @RequestBody AuthorSignUp dto) {
        Author author = authorService.updateAuthor(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Author updated successfully", author));
    }

    /**
     * Delete an author by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(ApiResponse.success("Author deleted successfully", null));
    }
}
