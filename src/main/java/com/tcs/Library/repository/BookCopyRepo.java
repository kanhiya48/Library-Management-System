package com.tcs.Library.repository;

import com.tcs.Library.entity.BookCopy;
import com.tcs.Library.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepo extends JpaRepository<BookCopy, Long> {
    Optional<BookCopy> findFirstByBookIdAndStatus(Long bookId, BookStatus status);

    List<BookCopy> findByBookIdAndStatus(Long bookId, BookStatus status);

    Optional<BookCopy> findByCopyPublicId(String copyPublicId);

    int countByBookIdAndStatus(Long bookId, BookStatus status);

    // For dashboard stats
    long countByStatus(BookStatus status);
}
