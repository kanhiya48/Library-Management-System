package com.tcs.Library.repository;

import com.tcs.Library.entity.IssuedBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcs.Library.enums.BookType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

@Repository
public interface IssuedBooksRepo extends JpaRepository<IssuedBooks, Long> {

        // Find active borrow for a specific book copy
        Optional<IssuedBooks> findByBookCopyIdAndStatus(Long bookCopyId, String status);

        // Check if user already has this book type borrowed
        @Query("SELECT COUNT(ib) > 0 FROM IssuedBooks ib WHERE ib.user.id = :userId AND ib.bookCopy.book.id = :bookId AND ib.status = 'BORROWED'")
        boolean existsActiveBorrowByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

        // Find active borrow record for user and book
        @Query("SELECT ib FROM IssuedBooks ib WHERE ib.user.id = :userId AND ib.bookCopy.book.id = :bookId AND ib.status = 'BORROWED'")
        Optional<IssuedBooks> findActiveByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);

        // Find all active borrows for a user
        List<IssuedBooks> findByUserIdAndStatus(Long userId, String status);

        // Find all borrows for a user with multiple statuses
        List<IssuedBooks> findByUserIdAndStatusIn(Long userId, List<String> statuses);

        // Find all borrows for a user (history)
        List<IssuedBooks> findByUserId(Long userId);

        // Find overdue books (for cron job)
        @Query("SELECT ib FROM IssuedBooks ib WHERE ib.status = 'BORROWED' AND ib.dueDate < :today")
        List<IssuedBooks> findOverdueBooks(@Param("today") LocalDate today);

        // Find overdue books for a specific user
        @Query("SELECT ib FROM IssuedBooks ib WHERE ib.user.id = :userId AND ib.status = 'BORROWED' AND ib.dueDate < :today")
        List<IssuedBooks> findOverdueBooksForUser(@Param("userId") Long userId, @Param("today") LocalDate today);

        // Find severely overdue (30+ days) for defaulter marking
        @Query("SELECT ib FROM IssuedBooks ib WHERE ib.status = 'BORROWED' AND ib.dueDate < :cutoffDate")
        List<IssuedBooks> findSeverelyOverdueBooks(@Param("cutoffDate") LocalDate cutoffDate);

        // Get all active borrows
        List<IssuedBooks> findByStatus(String status);

        // Count active borrows for a user
        int countByUserIdAndStatus(Long userId, String status);

        // For dashboard stats
        long countByStatus(String status);

        @Query("SELECT COUNT(ib) FROM IssuedBooks ib WHERE ib.status = 'BORROWED' AND ib.dueDate < :today")
        long countOverdueBooks(@Param("today") LocalDate today);

        long countByIssueDateAndStatus(LocalDate issueDate, String status);

        long countByReturnDate(LocalDate returnDate);

        // Find active borrowers for a book (for admin view)
        @Query("SELECT ib FROM IssuedBooks ib WHERE ib.bookCopy.book.id = :bookId AND ib.status = 'BORROWED'")
        List<IssuedBooks> findActiveBorrowsByBookId(@Param("bookId") Long bookId);

        // Check if book has any active borrows
        @Query("SELECT COUNT(ib) > 0 FROM IssuedBooks ib WHERE ib.bookCopy.book.id = :bookId AND ib.status = 'BORROWED'")
        boolean existsActiveBorrowsForBook(@Param("bookId") Long bookId);

        // Find all borrowed books with pagination and filters (for admin view)
        @Query("SELECT ib FROM IssuedBooks ib WHERE " +
                        "(:memberName IS NULL OR LOWER(ib.user.customerName) LIKE LOWER(CONCAT('%', :memberName, '%'))) AND "
                        +
                        "(:category IS NULL OR ib.bookCopy.book.category = :category) AND " +
                        "(:status IS NULL OR ib.status = :status) AND " +
                        "(:startDate IS NULL OR ib.issueDate >= :startDate) AND " +
                        "(:endDate IS NULL OR ib.issueDate <= :endDate)")
        org.springframework.data.domain.Page<IssuedBooks> findAllWithFilters(
                        @Param("memberName") String memberName,
                        @Param("category") BookType category,
                        @Param("status") String status,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        org.springframework.data.domain.Pageable pageable);

        // Find all borrowed books with pagination (for admin view)
        org.springframework.data.domain.Page<IssuedBooks> findByStatusIn(java.util.List<String> statuses,
                        org.springframework.data.domain.Pageable pageable);
}
