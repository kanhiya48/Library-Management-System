package com.tcs.Library.repository;

import com.tcs.Library.entity.IssuedBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssuedBooksRepo extends JpaRepository<IssuedBooks, Long> {

    // Find active borrow for a specific book copy
    Optional<IssuedBooks> findByBookCopyIdAndStatus(Long bookCopyId, String status);

    // Check if user already has this book type borrowed
    @Query("SELECT COUNT(ib) > 0 FROM IssuedBooks ib WHERE ib.user.id = :userId AND ib.bookCopy.book.id = :bookId AND ib.status = 'BORROWED'")
    boolean existsActiveBorrowByUserAndBook(Long userId, Long bookId);

    // Find all active borrows for a user
    List<IssuedBooks> findByUserIdAndStatus(Long userId, String status);

    // Find overdue books (for cron job)
    @Query("SELECT ib FROM IssuedBooks ib WHERE ib.status = 'BORROWED' AND ib.dueDate < :today")
    List<IssuedBooks> findOverdueBooks(LocalDate today);

    // Find severely overdue (30+ days) for defaulter marking
    @Query("SELECT ib FROM IssuedBooks ib WHERE ib.status = 'BORROWED' AND ib.dueDate < :cutoffDate")
    List<IssuedBooks> findSeverelyOverdueBooks(LocalDate cutoffDate);

    // Get all active borrows
    List<IssuedBooks> findByStatus(String status);

    // Count active borrows for a user
    int countByUserIdAndStatus(Long userId, String status);
}
