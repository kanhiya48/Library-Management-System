package com.tcs.Library.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.Library.dto.BookCreateRequest;
import com.tcs.Library.dto.BookDTO;
import com.tcs.Library.dto.wrapper.BookMapper;
import com.tcs.Library.entity.Author;
import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.BookCopy;
import com.tcs.Library.enums.BookStatus;
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

    /**
     * Create book using author IDs (legacy method).
     */
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
     * Create book using author emails.
     * Authors are looked up by email. If not found, they are created.
     * 
     * @param request BookCreateRequest with author emails
     * @return Created book with copies
     */
    @Transactional
    public Book createBookWithAuthors(BookCreateRequest request) {
        if (request.getAuthors() == null || request.getAuthors().isEmpty()) {
            throw new NoAuthorFoundException("At least one author is required");
        }

        Set<Author> authors = new HashSet<>();

        for (BookCreateRequest.AuthorInfo authorInfo : request.getAuthors()) {
            String email = authorInfo.getEmail();
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Author email cannot be blank");
            }

            // Find existing author or create new one
            Author author = authorRepo.findByEmail(email).orElseGet(() -> {
                if (authorInfo.getName() == null || authorInfo.getName().isBlank()) {
                    throw new IllegalArgumentException(
                            "Author name is required for new author with email: " + email);
                }
                Author newAuthor = new Author();
                newAuthor.setEmail(email);
                newAuthor.setName(authorInfo.getName());
                return authorRepo.save(newAuthor);
            });

            authors.add(author);
        }

        // Create the book
        Book book = new Book();
        book.setBookTitle(request.getBookTitle());
        book.setCategory(request.getCategory());
        book.setCoverUrl(request.getCoverUrl());
        book.setTotalCopies(request.getQuantity());
        book.setAuthors(authors);

        // Create book copies
        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < request.getQuantity(); i++) {
            BookCopy copy = new BookCopy();
            copy.setBook(book);
            copy.setStatus(BookStatus.AVAILABLE);
            copies.add(copy);
        }
        book.setCopies(copies);

        return bookRepo.save(book);
    }

    /**
     * Search books by keyword (matches title OR author name) - non-paginated
     */
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepo.findAll();
        }
        return bookRepo.searchByKeyword(keyword.trim());
    }

    /**
     * Search books by keyword with pagination
     */
    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepo.findAll(pageable);
        }
        return bookRepo.searchByKeyword(keyword.trim(), pageable);
    }

    /**
     * Search books by title only - non-paginated
     */
    public List<Book> searchByTitle(String title) {
        return bookRepo.findByBookTitleContainingIgnoreCase(title);
    }

    /**
     * Search books by title with pagination
     */
    public Page<Book> searchByTitle(String title, Pageable pageable) {
        return bookRepo.findByBookTitleContainingIgnoreCase(title, pageable);
    }

    /**
     * Search books by author name only - non-paginated
     */
    public List<Book> searchByAuthor(String authorName) {
        return bookRepo.findByAuthorNameContaining(authorName);
    }

    /**
     * Search books by author with pagination
     */
    public Page<Book> searchByAuthor(String authorName, Pageable pageable) {
        return bookRepo.findByAuthorNameContaining(authorName, pageable);
    }

    public Book getBookByPubId(String pubId) {
        return bookRepo.findByPublicId(pubId)
                .orElseThrow(() -> new BookNotFoundException(pubId));
    }

    /**
     * Get all books without pagination (legacy support)
     */
    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    /**
     * Get all books with pagination
     */
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepo.findAll(pageable);
    }
}
