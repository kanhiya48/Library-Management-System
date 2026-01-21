package com.tcs.Library.service;

import com.tcs.Library.dto.IssueBookRequest;
import com.tcs.Library.entity.*;
import com.tcs.Library.enums.BookStatus;
import com.tcs.Library.error.*;
import com.tcs.Library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowService {

    private final BookRepo bookRepo;
    private final UserRepo userRepo;
    private final BookCopyRepo bookCopyRepo;
    private final IssuedBooksRepo issuedBooksRepo;
    private final FineRepo fineRepo;

    private static final int LOAN_PERIOD_DAYS = 14;
    private static final int MAX_BOOKS_PER_USER = 5;
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("10.00");
    private static final BigDecimal DEFAULTER_FINE_THRESHOLD = new BigDecimal("100.00");
    private static final int DEFAULTER_OVERDUE_DAYS = 30;

    @Transactional
    public IssuedBooks issueBook(IssueBookRequest request) {
        // 1. Get User
        User user = userRepo.findByPublicId(request.getUserPublicId())
                .orElseThrow(() -> new NoUserFoundException("User not found: " + request.getUserPublicId()));

        // 2. Check if user is a defaulter
        if (user.isDefaulter()) {
            throw new UserIsDefaulterException(
                    "User is a defaulter and cannot borrow books. Please pay pending fines.");
        }

        // 3. Check if user has unpaid fines
        if (user.getTotalUnpaidFine().compareTo(BigDecimal.ZERO) > 0) {
            throw new UserIsDefaulterException(
                    "User has unpaid fines of ₹" + user.getTotalUnpaidFine() + ". Please clear dues before borrowing.");
        }

        // 4. Get Book
        Book book = bookRepo.findByPublicId(request.getBookPublicId())
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + request.getBookPublicId()));

        // 5. Check max books per user limit
        int currentBorrows = issuedBooksRepo.countByUserIdAndStatus(user.getId(), "BORROWED");
        if (currentBorrows >= MAX_BOOKS_PER_USER) {
            throw new MaxBooksExceededException(
                    "Maximum limit of " + MAX_BOOKS_PER_USER + " books reached. Return a book to borrow more.");
        }

        // 6. Check unique book constraint - user cannot borrow same book type twice
        boolean alreadyBorrowed = issuedBooksRepo.existsActiveBorrowByUserAndBook(user.getId(), book.getId());
        if (alreadyBorrowed) {
            throw new DuplicateBookBorrowException(
                    "User already has an active borrow for book: " + book.getBookTitle());
        }

        // 6. Find available copy (with optimistic locking via @Version)
        BookCopy availableCopy = bookCopyRepo.findFirstByBookIdAndStatus(book.getId(), BookStatus.AVAILABLE)
                .orElseThrow(() -> new NoCopyAvailableException("No copies available for: " + book.getBookTitle()));

        // 7. Update copy status - triggers version check on save
        availableCopy.setStatus(BookStatus.BORROWED);
        availableCopy.setCurrentUser(user);
        bookCopyRepo.save(availableCopy);

        // 8. Create issue record
        IssuedBooks issuedBook = new IssuedBooks();
        issuedBook.setUser(user);
        issuedBook.setBookCopy(availableCopy);
        issuedBook.setIssueDate(LocalDate.now());
        issuedBook.setDueDate(LocalDate.now().plusDays(LOAN_PERIOD_DAYS));
        issuedBook.setStatus("BORROWED");
        issuedBook.setFineAmount(BigDecimal.ZERO);

        log.info("Book issued: {} to user: {}", book.getBookTitle(), user.getEmail());
        return issuedBooksRepo.save(issuedBook);
    }

    /**
     * Return a book by copy ID.
     */
    @Transactional
    public IssuedBooks returnBook(Long bookCopyId) {
        // 1. Get the book copy
        BookCopy copy = bookCopyRepo.findById(bookCopyId)
                .orElseThrow(() -> new BookNotFoundException("Book copy not found: " + bookCopyId));

        if (copy.getStatus() != BookStatus.BORROWED) {
            throw new InvalidBookOperationException("Book copy is not currently borrowed");
        }

        // 2. Find the active issue record
        IssuedBooks record = issuedBooksRepo.findByBookCopyIdAndStatus(bookCopyId, "BORROWED")
                .orElseThrow(() -> new InvalidBookOperationException("No active borrow record found for this copy"));

        return processReturn(record, copy);
    }

    /**
     * Return book by issue record ID.
     */
    @Transactional
    public IssuedBooks returnBookByRecordId(Long recordId) {
        IssuedBooks record = issuedBooksRepo.findById(recordId)
                .orElseThrow(() -> new InvalidBookOperationException("Borrow record not found: " + recordId));

        if (!"BORROWED".equals(record.getStatus())) {
            throw new InvalidBookOperationException("This book has already been returned");
        }

        BookCopy copy = record.getBookCopy();
        return processReturn(record, copy);
    }

    /**
     * Return book by user - user returns their own book using book public ID.
     */
    @Transactional
    public IssuedBooks returnBookByUser(User user, String bookPublicId) {
        // Find the book
        Book book = bookRepo.findByPublicId(bookPublicId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookPublicId));

        // Find the active borrow record for this user and book
        IssuedBooks record = issuedBooksRepo.findActiveByUserAndBook(user.getId(), book.getId())
                .orElseThrow(() -> new InvalidBookOperationException(
                        "You don't have an active borrow for: " + book.getBookTitle()));

        BookCopy copy = record.getBookCopy();
        return processReturn(record, copy);
    }

    /**
     * Get all currently borrowed books for a user.
     */
    public List<IssuedBooks> getUserBorrowedBooks(User user) {
        return issuedBooksRepo.findByUserIdAndStatus(user.getId(), "BORROWED");
    }

    /**
     * Get borrow history for a user.
     */
    public List<IssuedBooks> getUserBorrowHistory(User user) {
        return issuedBooksRepo.findByUserId(user.getId());
    }

    /**
     * Get all overdue books for a user.
     */
    public List<IssuedBooks> getUserOverdueBooks(User user) {
        return issuedBooksRepo.findOverdueBooksForUser(user.getId(), LocalDate.now());
    }

    /**
     * Common return processing logic.
     */
    private IssuedBooks processReturn(IssuedBooks record, BookCopy copy) {
        User user = record.getUser();
        LocalDate today = LocalDate.now();

        // Calculate fine if overdue
        if (today.isAfter(record.getDueDate())) {
            long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), today);
            BigDecimal fineAmount = FINE_PER_DAY.multiply(BigDecimal.valueOf(daysOverdue));
            record.setFineAmount(fineAmount);

            // Create fine record
            Fine fine = new Fine();
            fine.setUser(user);
            fine.setIssuedBook(record);
            fine.setAmount(fineAmount);
            fine.setPaid(false);
            fineRepo.save(fine);

            // Update user's total unpaid fine
            user.setTotalUnpaidFine(user.getTotalUnpaidFine().add(fineAmount));

            // Check and mark as defaulter if needed
            checkAndUpdateDefaulterStatus(user);
            userRepo.save(user);

            log.info("Fine of ₹{} applied for {} days overdue", fineAmount, daysOverdue);
        }

        // Update copy status
        copy.setStatus(BookStatus.AVAILABLE);
        copy.setCurrentUser(null);
        bookCopyRepo.save(copy);

        // Update issue record
        record.setReturnDate(today);
        record.setStatus("RETURNED");

        log.info("Book returned: {} by user {}", copy.getBook().getBookTitle(), user.getEmail());
        return issuedBooksRepo.save(record);
    }

    public void checkAndUpdateDefaulterStatus(User user) {
        // Mark as defaulter if unpaid fine exceeds threshold
        if (user.getTotalUnpaidFine().compareTo(DEFAULTER_FINE_THRESHOLD) > 0) {
            user.setDefaulter(true);
            log.warn("User {} marked as defaulter due to high unpaid fines: ₹{}",
                    user.getEmail(), user.getTotalUnpaidFine());
        }

        // Also check for severely overdue books
        LocalDate cutoffDate = LocalDate.now().minusDays(DEFAULTER_OVERDUE_DAYS);
        var overdueBooks = issuedBooksRepo.findByUserIdAndStatus(user.getId(), "BORROWED")
                .stream()
                .filter(ib -> ib.getDueDate().isBefore(cutoffDate))
                .toList();

        if (!overdueBooks.isEmpty()) {
            user.setDefaulter(true);
            log.warn("User {} marked as defaulter due to {} books overdue by 30+ days",
                    user.getEmail(), overdueBooks.size());
        }
    }
}
