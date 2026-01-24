package com.tcs.Library.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.tcs.Library.dto.BookCreateRequest;
import com.tcs.Library.dto.IssueBookRequest;
import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.Fine;
import com.tcs.Library.entity.IssuedBooks;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.BookType;
import com.tcs.Library.enums.Role;
import com.tcs.Library.repository.BookRepo;
import com.tcs.Library.repository.FineRepo;
import com.tcs.Library.repository.IssuedBooksRepo;
import com.tcs.Library.repository.UserRepo;
import com.tcs.Library.service.BookService;
import com.tcs.Library.service.BorrowService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstStartData {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final BookRepo bookRepo;
    private final BookService bookService;
    private final BorrowService borrowService;
    private final FineRepo fineRepo;
    private final IssuedBooksRepo issuedBooksRepo;

    @PostConstruct
    public void createSystemUsersOnStart() {
        // Create Admin
        createUserIfNotExists(
                "admin@library.com",
                "admin@123",
                "System Admin",
                Set.of(Role.ADMIN));

        // Create Staff 1
        createUserIfNotExists(
                "staff1@library.com",
                "staff@123",
                "Staff Member 1",
                Set.of(Role.STAFF));

        // Create Staff 2
        createUserIfNotExists(
                "staff2@library.com",
                "staff@123",
                "Staff Member 2",
                Set.of(Role.STAFF));

        // Create Test User
        createUserIfNotExists(
                "test@library.com",
                "test@123",
                "Test User",
                Set.of(Role.USER));

        log.info("System users initialized: 1 Admin, 2 Staff");

        createSampleBooks();
        issueSampleBookToTestUser();
        createSampleFineForTestUser();
    }

    private void createUserIfNotExists(String email, String password, String name, Set<Role> roles) {
        if (!userRepo.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setCustomerName(name);
            user.setRoles(new HashSet<>(roles));
            userRepo.save(user);
            log.info("Created system user: {} with roles: {}", email, roles);
        }
    }

    private void createSampleBooks() {
        if (bookRepo.count() > 0) {
            log.info("Books already exist, skipping sample data creation.");
            return;
        }

        // Define Authors
        BookCreateRequest.AuthorInfo rowling = new BookCreateRequest.AuthorInfo("jk@rowling.com", "J.K. Rowling");
        BookCreateRequest.AuthorInfo martin = new BookCreateRequest.AuthorInfo("grrm@westeros.com",
                "George R.R. Martin");
        BookCreateRequest.AuthorInfo tolkien = new BookCreateRequest.AuthorInfo("jrrt@tolkien.com", "J.R.R. Tolkien");
        BookCreateRequest.AuthorInfo asimov = new BookCreateRequest.AuthorInfo("isaac@asimov.com", "Isaac Asimov");
        BookCreateRequest.AuthorInfo christie = new BookCreateRequest.AuthorInfo("agatha@christie.com",
                "Agatha Christie");

        // Create Books
        createBook("Harry Potter and the Philosopher's Stone", BookType.FICTION, 5, rowling);
        createBook("Harry Potter and the Chamber of Secrets", BookType.FICTION, 4, rowling);
        createBook("Harry Potter and the Prisoner of Azkaban", BookType.FICTION, 6, rowling);

        createBook("A Game of Thrones", BookType.FICTION, 3, martin);
        createBook("A Clash of Kings", BookType.FICTION, 3, martin);
        createBook("A Storm of Swords", BookType.FICTION, 2, martin);

        createBook("The Hobbit", BookType.FICTION, 10, tolkien);
        createBook("The Fellowship of the Ring", BookType.FICTION, 8, tolkien);
        createBook("The Two Towers", BookType.FICTION, 8, tolkien);
        createBook("The Return of the King", BookType.FICTION, 8, tolkien);

        createBook("Foundation", BookType.SCIFI, 5, asimov);
        createBook("I, Robot", BookType.SCIFI, 4, asimov);
        createBook("The Caves of Steel", BookType.SCIFI, 3, asimov);

        createBook("Murder on the Orient Express", BookType.FICTION, 6, christie);
        createBook("And Then There Were None", BookType.FICTION, 7, christie);

        log.info("15 Sample books initialized.");
    }

    private void createBook(String title, BookType type, int quantity, BookCreateRequest.AuthorInfo author) {
        BookCreateRequest request = new BookCreateRequest();
        request.setBookTitle(title);
        request.setCategory(type);
        request.setQuantity(quantity);
        request.setAuthors(Set.of(author));
        // Placeholder image or empty
        request.setCoverUrl("https://placehold.co/400x600?text=" + title.replace(" ", "+"));

        try {
            bookService.createBookWithAuthors(request);
        } catch (Exception e) {
            log.error("Failed to create sample book: {}", title, e);
        }
    }

    private void issueSampleBookToTestUser() {
        try {
            log.info("Attempting to issue sample book to test user...");

            // 1. Find Test User
            User testUser = userRepo.findByEmailIgnoreCase("test@library.com").orElse(null);
            if (testUser == null) {
                log.warn("Test user 'test@library.com' not found. Skipping sample issue.");
                return;
            }
            log.info("Found Test User: {}", testUser.getEmail());

            // 2. Find Book
            Book book = bookRepo.findByBookTitleContainingIgnoreCase("The Hobbit").stream().findFirst().orElse(null);
            if (book == null) {
                log.warn("Sample book 'The Hobbit' not found. Skipping sample issue.");
                return;
            }
            log.info("Found Book: {} (Public ID: {})", book.getBookTitle(), book.getPublicId());

            // 3. Issue Book
            if (borrowService.getUserBorrowedBooks(testUser).isEmpty()) {
                IssueBookRequest request = new IssueBookRequest();
                request.setUserPublicId(testUser.getPublicId());
                request.setBookPublicId(book.getPublicId());

                log.info("Issuing book to user...");
                borrowService.issueBook(request);
                log.info("Successfully issued 'The Hobbit' to Test User.");
            } else {
                log.info("Test User already has borrowed books. Skipping issue.");
            }
        } catch (Exception e) {
            log.error("Failed to issue sample book to test user", e);
        }
    }

    private void createSampleFineForTestUser() {
        try {
            log.info("Attempting to create sample fine for test user...");

            // 1. Find Test User
            User testUser = userRepo.findByEmailIgnoreCase("test@library.com").orElse(null);
            if (testUser == null) {
                log.warn("Test user 'test@library.com' not found. Skipping sample fine creation.");
                return;
            }
            log.info("Found Test User: {}", testUser.getEmail());

            // 2. Check if fine already exists
            if (fineRepo.findByUserId(testUser.getId()).stream()
                    .anyMatch(f -> f.getAmount().compareTo(new BigDecimal("120")) == 0)) {
                log.info("Test user already has a fine of 120. Skipping fine creation.");
                return;
            }

            // 3. Find an issued book for the fine
            IssuedBooks issuedBook = issuedBooksRepo.findByUserIdAndStatus(testUser.getId(), "BORROWED")
                    .stream()
                    .findFirst()
                    .orElse(null);

            // If no borrowed book, issue one first
            if (issuedBook == null) {
                log.info("Test user has no borrowed books. Issuing a book first...");

                Book book = bookRepo.findByBookTitleContainingIgnoreCase("Harry Potter and the Chamber of Secrets")
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (book == null) {
                    log.warn("Could not find a book to issue. Skipping fine creation.");
                    return;
                }

                try {
                    IssueBookRequest request = new IssueBookRequest();
                    request.setUserPublicId(testUser.getPublicId());
                    request.setBookPublicId(book.getPublicId());

                    borrowService.issueBook(request);
                    log.info("Successfully issued '{}' to Test User for fine creation.", book.getBookTitle());

                    // Fetch the newly created issued book
                    issuedBook = issuedBooksRepo.findByUserIdAndStatus(testUser.getId(), "BORROWED")
                            .stream()
                            .findFirst()
                            .orElse(null);

                    if (issuedBook == null) {
                        log.error("Failed to retrieve issued book after issuing. Cannot create fine.");
                        return;
                    }
                } catch (Exception e) {
                    log.error("Failed to issue book for fine creation: {}", e.getMessage());
                    return;
                }
            }

            // 4. Create Fine
            Fine fine = new Fine();
            fine.setUser(testUser);
            fine.setIssuedBook(issuedBook);
            fine.setAmount(new BigDecimal("120"));
            fine.setPaid(false);
            fine.setCreatedAt(LocalDate.now());

            fineRepo.save(fine);

            // Update user's total unpaid fine
            testUser.setTotalUnpaidFine(testUser.getTotalUnpaidFine().add(new BigDecimal("120")));
            userRepo.save(testUser);

            log.info("Successfully created fine of 120 for Test User.");
        } catch (Exception e) {
            log.error("Failed to create sample fine for test user", e);
        }
    }
}
