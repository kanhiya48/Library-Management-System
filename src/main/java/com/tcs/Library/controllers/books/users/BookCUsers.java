package com.tcs.Library.controllers.books.users;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.entity.Book;
import com.tcs.Library.service.AuthorService;
import com.tcs.Library.service.BookService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookCUsers {

    private final BookService bookService;
    private final AuthorService authorService;

    /**
     * Search books by keyword (title OR author name)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Book>>> searchBooks(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Search query cannot be empty"));
        }
        List<Book> results = bookService.searchBooks(q);
        return ResponseEntity.ok(ApiResponse.success("Found " + results.size() + " books", results));
    }

    /**
     * Search books by title only
     */
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<List<Book>>> searchByTitle(@RequestParam String title) {
        List<Book> results = bookService.searchByTitle(title);
        return ResponseEntity.ok(ApiResponse.success("Found " + results.size() + " books by title", results));
    }

    /**
     * Search books by author name only
     */
    @GetMapping("/search/author")
    public ResponseEntity<ApiResponse<List<Book>>> searchByAuthor(@RequestParam String name) {
        List<Book> results = bookService.searchByAuthor(name);
        return ResponseEntity.ok(ApiResponse.success("Found " + results.size() + " books by author", results));
    }

    /**
     * Get all books
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(ApiResponse.success("All books retrieved", books));
    }

    /**
     * Get books by author ID
     */
    @GetMapping("/author/{id}")
    public ResponseEntity<ApiResponse<List<Book>>> getBooksByAuthorId(@PathVariable Long id) {
        List<Book> books = authorService.getByAuthorId(id);
        return ResponseEntity.ok(ApiResponse.success("Books by author retrieved", books));
    }

    /**
     * Get book by public ID
     */
    @GetMapping("/{publicId}")
    public ResponseEntity<ApiResponse<Book>> getBookByPublicId(@PathVariable String publicId) {
        Book book = bookService.getBookByPubId(publicId);
        return ResponseEntity.ok(ApiResponse.success("Book found", book));
    }
}
