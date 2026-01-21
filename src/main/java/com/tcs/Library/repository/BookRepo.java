package com.tcs.Library.repository;

import com.tcs.Library.entity.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {
    Optional<Book> findByPublicId(String publicId);

    Optional<Book> findByBookTitleIgnoreCase(String bookTitle);

    boolean existsByBookTitle(String bookTitle);

    // Search by title (case-insensitive, contains)
    List<Book> findByBookTitleContainingIgnoreCase(String title);

    // Search by title with pagination
    Page<Book> findByBookTitleContainingIgnoreCase(String title, Pageable pageable);

    // Search by author name
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContaining(@Param("authorName") String authorName);

    // Search by author name with pagination
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    Page<Book> findByAuthorNameContaining(@Param("authorName") String authorName, Pageable pageable);

    // Combined search (title OR author)
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.authors a WHERE LOWER(b.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    // Combined search with pagination
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.authors a WHERE LOWER(b.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Get all books with pagination
    Page<Book> findAll(Pageable pageable);
}
