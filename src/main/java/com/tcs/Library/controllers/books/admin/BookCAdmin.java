package com.tcs.Library.controllers.books.admin;

import com.tcs.Library.dto.BookDTO;
import com.tcs.Library.entity.Book;
import com.tcs.Library.error.NoAuthorFoundException;
import com.tcs.Library.service.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BookCAdmin {
    private final BookService bookSvc;

    @PostMapping("/books/add-book")
    public ResponseEntity<?> addBookController(@RequestBody BookDTO dto) {
        return ResponseEntity.ok(bookSvc.createBook(dto));
    }
}
