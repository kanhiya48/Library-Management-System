package com.tcs.Library.repository;

import com.tcs.Library.entity.Book;
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

    // Search by author name
    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContaining(@Param("authorName") String authorName);

    // Combined search (title OR author)
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.authors a WHERE LOWER(b.bookTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByKeyword(@Param("keyword") String keyword);
}
