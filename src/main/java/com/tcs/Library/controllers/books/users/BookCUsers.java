package com.tcs.Library.controllers.books.users;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.PagedResponse;
import com.tcs.Library.entity.Book;
import com.tcs.Library.service.AuthorService;
import com.tcs.Library.service.BookService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookCUsers {

    private final BookService bookService;
    private final AuthorService authorService;

    /**
     * Search books by keyword (title OR author name) with pagination.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<Book>>> searchBooks(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookTitle") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Search query cannot be empty"));
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> results = bookService.searchBooks(q, pageable);
        return ResponseEntity.ok(ApiResponse.success("Found " + results.getTotalElements() + " books",
                PagedResponse.from(results)));
    }

    /**
     * Search books by title only with pagination.
     */
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<PagedResponse<Book>>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> results = bookService.searchByTitle(title, pageable);
        return ResponseEntity.ok(ApiResponse.success("Found " + results.getTotalElements() + " books by title",
                PagedResponse.from(results)));
    }

    /**
     * Search books by author name only with pagination.
     */
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<PagedResponse<Book>>> searchByAuthor(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> results = bookService.searchByAuthor(name, pageable);
        return ResponseEntity.ok(ApiResponse.success("Found " + results.getTotalElements() + " books by author",
                PagedResponse.from(results)));
    }

    /**
     * Get all books with pagination.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<Book>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookTitle") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success("Books retrieved", PagedResponse.from(books)));
    }

    /**
     * Get books by author ID.
     */
    @GetMapping("/author/{id}")
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByAuthorId(@PathVariable Long id) {
        List<Book> books = authorService.getByAuthorId(id);
        return ResponseEntity.ok(ApiResponse.success("Books by author retrieved", books));
    }

    /**
     * Get book by public ID.
     */
    @GetMapping("/{publicId}")
    public ResponseEntity<ApiResponse<Book>> getBookByPublicId(@PathVariable String publicId) {
        Book book = bookService.getBookByPubId(publicId);
        return ResponseEntity.ok(ApiResponse.success("Book found", book));
    }
}
