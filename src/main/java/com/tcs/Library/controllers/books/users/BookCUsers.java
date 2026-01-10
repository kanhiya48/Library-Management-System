package com.tcs.Library.controllers.books.users;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tcs.Library.entity.Author;
import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.User;
import com.tcs.Library.service.AuthorService;
import com.tcs.Library.service.BookService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/user")
public class BookCUsers {

    @Autowired
    private BookService svc;
    @Autowired
    private AuthorService author_svc;

    @GetMapping("/search/{key}")
    public ResponseEntity<List<Book>> fuzzySearch(@PathVariable("key") String key) {
        if (key == null || key.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(svc.searchBooks(key));
    }

    @GetMapping("/book/author/{id}")
    public ResponseEntity<List<Book>> getBookByAuthor(@PathVariable("id") Long id) {
        return ResponseEntity.ok(author_svc.getByAuthorId(id));
    }

    @GetMapping("/borrow/book/{pub_id}")
    public ResponseEntity<?> borrowBook(@PathVariable("pub_id") String param,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(null);
    }



}
