package com.tcs.Library.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.Library.entity.Author;
import com.tcs.Library.entity.Book;
import com.tcs.Library.repository.AuthorRepo;
import com.tcs.Library.dto.AuthorSignUp;
import com.tcs.Library.dto.wrapper.AuthorMapper;
import com.tcs.Library.error.DuplicateAuthorException;
import com.tcs.Library.error.NoAuthorFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepo authorRepo;

    public List<Book> getByAuthorId(Long id) {
        Author author = authorRepo.findById(id)
                .orElseThrow(() -> new NoAuthorFoundException("No Author for ID: " + id));
        return author.getBook();
    }

    /**
     * Register a new author. Email must be unique.
     */
    @Transactional
    public Author registerAuthor(AuthorSignUp dto) {
        // Check if email already exists
        if (dto.getEmail() != null && authorRepo.existsByEmail(dto.getEmail())) {
            throw new DuplicateAuthorException(dto.getEmail());
        }

        Author author = AuthorMapper.toEntity(dto);
        return authorRepo.save(author);
    }

    /**
     * Find author by email.
     */
    public Optional<Author> findByEmail(String email) {
        return authorRepo.findByEmail(email);
    }

    /**
     * Find author by email, throw exception if not found.
     */
    public Author getByEmail(String email) {
        return authorRepo.findByEmail(email)
                .orElseThrow(() -> new NoAuthorFoundException("No Author for email: " + email));
    }

    /**
     * Get or create author by email.
     * If author with email exists, return it. Otherwise, create new.
     */
    @Transactional
    public Author getOrCreateByEmail(AuthorSignUp dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Author email is required");
        }

        return authorRepo.findByEmail(dto.getEmail())
                .orElseGet(() -> {
                    Author author = AuthorMapper.toEntity(dto);
                    return authorRepo.save(author);
                });
    }

    /**
     * Get all authors without pagination.
     */
    public List<Author> getAllAuthors() {
        return authorRepo.findAll();
    }

    /**
     * Get all authors with pagination.
     */
    public Page<Author> getAllAuthors(Pageable pageable) {
        return authorRepo.findAll(pageable);
    }

    /**
     * Search authors by name without pagination.
     */
    public List<Author> searchByName(String name) {
        return authorRepo.findByNameContainingIgnoreCase(name);
    }

    /**
     * Search authors by name with pagination.
     */
    public Page<Author> searchByName(String name, Pageable pageable) {
        return authorRepo.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Search authors by name OR email with pagination.
     */
    public Page<Author> searchByNameOrEmail(String query, Pageable pageable) {
        return authorRepo.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable);
    }

    /**
     * Get author by ID.
     */
    public Author getById(Long id) {
        return authorRepo.findById(id)
                .orElseThrow(() -> new NoAuthorFoundException("No Author for ID: " + id));
    }

    /**
     * Check if author email exists.
     */
    public boolean emailExists(String email) {
        return authorRepo.existsByEmail(email);
    }

    /**
     * Update an existing author.
     */
    @Transactional
    public Author updateAuthor(Long id, AuthorSignUp dto) {
        Author author = authorRepo.findById(id)
                .orElseThrow(() -> new NoAuthorFoundException("No Author for ID: " + id));

        // Check if email is being changed to an existing one
        if (dto.getEmail() != null && !dto.getEmail().equals(author.getEmail())) {
            if (authorRepo.existsByEmail(dto.getEmail())) {
                throw new DuplicateAuthorException(dto.getEmail());
            }
            author.setEmail(dto.getEmail());
        }

        if (dto.getName() != null) {
            author.setName(dto.getName());
        }

        return authorRepo.save(author);
    }

    /**
     * Delete an author by ID.
     */
    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepo.findById(id)
                .orElseThrow(() -> new NoAuthorFoundException("No Author for ID: " + id));

        // Check if author has books
        if (author.getBook() != null && !author.getBook().isEmpty()) {
            throw new IllegalStateException("Cannot delete author with associated books. Remove the books first.");
        }

        authorRepo.delete(author);
    }
}
