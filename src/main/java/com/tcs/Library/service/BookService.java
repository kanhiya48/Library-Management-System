package com.tcs.Library.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tcs.Library.dto.BookDTO;
import com.tcs.Library.dto.wrapper.BookMapper;
import com.tcs.Library.entity.Author;
import com.tcs.Library.entity.Book;
import com.tcs.Library.error.BookNotFoundException;
import com.tcs.Library.error.NoAuthorFoundException;
import com.tcs.Library.repository.AuthorRepo;
import com.tcs.Library.repository.BookRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepo bookRepo;
    private final AuthorRepo authorRepo;

    @Transactional
    public Book createBook(BookDTO dto) throws NoAuthorFoundException {
        Set<Long> aids = dto.getAuthorId();

        if (aids == null || aids.isEmpty()) {
            throw new NoAuthorFoundException("Author IDs are missing");
        }

        Set<Author> authorSet = new HashSet<>();
        authorRepo.findAllById(aids).forEach(authorSet::add);

        if (authorSet.size() != aids.size()) {
            throw new NoAuthorFoundException("One or more author IDs are invalid");
        }

        Book newBook = BookMapper.toEntity(dto, authorSet);
        return bookRepo.save(newBook);
    }

    /**
     * Search books by keyword (matches title OR author name)
     */
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepo.findAll();
        }
        return bookRepo.searchByKeyword(keyword.trim());
    }

    /**
     * Search books by title only
     */
    public List<Book> searchByTitle(String title) {
        return bookRepo.findByBookTitleContainingIgnoreCase(title);
    }

    /**
     * Search books by author name only
     */
    public List<Book> searchByAuthor(String authorName) {
        return bookRepo.findByAuthorNameContaining(authorName);
    }

    public Book getBookByPubId(String pubId) {
        return bookRepo.findByPublicId(pubId)
                .orElseThrow(() -> new BookNotFoundException(pubId));
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }
}
