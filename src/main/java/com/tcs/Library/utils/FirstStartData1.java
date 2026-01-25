package com.tcs.Library.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tcs.Library.entity.*;
import com.tcs.Library.enums.*;
import com.tcs.Library.repository.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstStartData1 {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final BookRepo bookRepo;
    private final AuthorRepo authorRepo;
    private final BookCopyRepo bookCopyRepo;
    private final IssuedBooksRepo issuedBooksRepo;
    private final FineRepo fineRepo;
    private final BookDonationRepo bookDonationRepo;
    private final ComplaintRepo complaintRepo;

    @PostConstruct
    public void createDummyDataOnStart() {
        if (userRepo.count() > 4) {
            log.info("Dummy data already exists, skipping FirstStartData1 creation.");
            return;
        }

        log.info("Starting to create comprehensive dummy data...");

        // Create Users (10+ users)
        List<User> users = createUsers();
        log.info("Created {} users", users.size());

        // Create Authors (10+ authors)
        List<Author> authors = createAuthors();
        log.info("Created {} authors", authors.size());

        // Create Books (10+ books)
        List<Book> books = createBooks(authors);
        log.info("Created {} books", books.size());

        // Create Book Copies (multiple copies for each book)
        List<BookCopy> bookCopies = createBookCopies(books, users);
        log.info("Created {} book copies", bookCopies.size());

        // Create Issued Books (10+ issued records)
        List<IssuedBooks> issuedBooks = createIssuedBooks(users, bookCopies);
        log.info("Created {} issued books", issuedBooks.size());

        // Create Fines (10+ fine records)
        createFines(users, issuedBooks);
        log.info("Created fines for users");

        // Create Book Donations (10+ donations)
        createBookDonations(users);
        log.info("Created book donations");

        // Create Complaints (10+ complaints)
        createComplaints(users);
        log.info("Created complaints");

        log.info("Comprehensive dummy data creation completed successfully!");
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();

        // Create 15 regular users
        for (int i = 1; i <= 15; i++) {
            User user = new User();
            user.setEmail("user" + i + "@library.com");
            user.setPasswordHash(passwordEncoder.encode("user@123"));
            user.setCustomerName("User " + i);
            user.setCountryCode("+91");
            user.setMobileNumber("98765432" + String.format("%02d", i));
            user.setAddress(i + " Main Street, City " + i + ", State");
            user.setDateOfBirth(LocalDate.of(1990 + i % 10, (i % 12) + 1, (i % 28) + 1));
            user.setSecretQuestion("What is your favorite color?");
            user.setSecretAnswerHash(passwordEncoder.encode("blue" + i));
            user.setDefaulter(i % 5 == 0); // Every 5th user is a defaulter
            user.setTotalUnpaidFine(i % 5 == 0 ? new BigDecimal("75.00").add(new BigDecimal(i * 5)) : BigDecimal.ZERO);
            user.setRoles(new HashSet<>(Set.of(Role.USER)));
            users.add(userRepo.save(user));
        }

        return users;
    }

    private List<Author> createAuthors() {
        List<Author> authors = new ArrayList<>();

        String[][] authorData = {
                { "Stephen King", "stephen@king.com" },
                { "Jane Austen", "jane@austen.com" },
                { "Mark Twain", "mark@twain.com" },
                { "Ernest Hemingway", "ernest@hemingway.com" },
                { "Virginia Woolf", "virginia@woolf.com" },
                { "Charles Dickens", "charles@dickens.com" },
                { "Leo Tolstoy", "leo@tolstoy.com" },
                { "Franz Kafka", "franz@kafka.com" },
                { "Gabriel Garcia Marquez", "gabriel@marquez.com" },
                { "Haruki Murakami", "haruki@murakami.com" },
                { "Toni Morrison", "toni@morrison.com" },
                { "Fyodor Dostoevsky", "fyodor@dostoevsky.com" }
        };

        for (String[] data : authorData) {
            if (!authorRepo.existsByEmail(data[1])) {
                Author author = new Author();
                author.setName(data[0]);
                author.setEmail(data[1]);
                authors.add(authorRepo.save(author));
            }
        }

        return authors;
    }

    private List<Book> createBooks(List<Author> authors) {
        List<Book> books = new ArrayList<>();

        Object[][] bookData = {
                { "The Shining", BookType.FICTION, 8, 0,
                        "A chilling tale of a haunted hotel and a family's descent into madness." },
                { "Pride and Prejudice", BookType.FICTION, 12, 1,
                        "A romantic novel of manners set in Georgian England." },
                { "The Adventures of Tom Sawyer", BookType.FICTION, 10, 2,
                        "A young boy's adventures along the Mississippi River." },
                { "The Old Man and the Sea", BookType.FICTION, 7, 3,
                        "An epic struggle between an old fisherman and a giant marlin." },
                { "Mrs Dalloway", BookType.FICTION, 6, 4,
                        "A day in the life of a high-society woman in post-WWI England." },
                { "Great Expectations", BookType.FICTION, 9, 5, "The coming of age story of an orphan named Pip." },
                { "War and Peace", BookType.HISTORY, 5, 6,
                        "An epic tale of Russian society during the Napoleonic era." },
                { "The Metamorphosis", BookType.FICTION, 8, 7,
                        "A man wakes up to find himself transformed into a giant insect." },
                { "One Hundred Years of Solitude", BookType.FICTION, 10, 8,
                        "The multi-generational story of the Buendía family." },
                { "Norwegian Wood", BookType.FICTION, 11, 9,
                        "A nostalgic story of loss and sexuality in 1960s Tokyo." },
                { "Beloved", BookType.FICTION, 7, 10, "A haunting story about the horrors of slavery." },
                { "Crime and Punishment", BookType.FICTION, 6, 11,
                        "A psychological drama about guilt and redemption." },
                { "It", BookType.FICTION, 9, 0, "A terrifying tale of a shape-shifting entity that feeds on fear." },
                { "Sense and Sensibility", BookType.FICTION, 8, 1,
                        "Two sisters navigate love and heartbreak in England." },
                { "The Prince and the Pauper", BookType.FICTION, 10, 2,
                        "A tale of two boys who switch places in Tudor England." }
        };

        for (Object[] data : bookData) {
            if (bookRepo.findByBookTitleContainingIgnoreCase((String) data[0]).isEmpty()) {
                Book book = new Book();
                book.setBookTitle((String) data[0]);
                book.setCategory((BookType) data[1]);
                book.setTotalCopies((Integer) data[2]);
                book.setCoverUrl("https://placehold.co/400x600?text=" + ((String) data[0]).replace(" ", "+"));
                book.setDescription((String) data[4]);

                // Add author
                Set<Author> bookAuthors = new HashSet<>();
                bookAuthors.add(authors.get((Integer) data[3]));
                book.setAuthors(bookAuthors);

                books.add(bookRepo.save(book));
            }
        }

        return books;
    }

    private List<BookCopy> createBookCopies(List<Book> books, List<User> users) {
        List<BookCopy> allCopies = new ArrayList<>();

        for (Book book : books) {
            List<BookCopy> bookCopies = new ArrayList<>();
            int totalCopies = book.getTotalCopies();

            for (int i = 0; i < totalCopies; i++) {
                BookCopy copy = new BookCopy();
                copy.setBook(book);

                // Set different statuses for variety
                if (i < totalCopies * 0.7) {
                    copy.setStatus(BookStatus.AVAILABLE);
                    copy.setCurrentUser(null);
                } else if (i < totalCopies * 0.9) {
                    copy.setStatus(BookStatus.BORROWED);
                    // Assign to a user
                    copy.setCurrentUser(users.get(i % Math.min(10, users.size())));
                } else {
                    copy.setStatus(i % 2 == 0 ? BookStatus.RESERVED : BookStatus.AVAILABLE);
                    copy.setCurrentUser(null);
                }

                bookCopies.add(bookCopyRepo.save(copy));
            }

            book.setCopies(bookCopies);
            allCopies.addAll(bookCopies);
        }

        return allCopies;
    }

    private List<IssuedBooks> createIssuedBooks(List<User> users, List<BookCopy> bookCopies) {
        List<IssuedBooks> issuedBooks = new ArrayList<>();

        // Filter only borrowed book copies
        List<BookCopy> borrowedCopies = bookCopies.stream()
                .filter(copy -> copy.getStatus() == BookStatus.BORROWED)
                .limit(20) // Create 20 issued book records
                .toList();

        int userIndex = 0;
        for (int i = 0; i < borrowedCopies.size(); i++) {
            BookCopy copy = borrowedCopies.get(i);
            IssuedBooks issued = new IssuedBooks();

            // Get a regular user (not staff or admin)
            User user = users.get(userIndex % 15);
            issued.setUser(user);
            issued.setBookCopy(copy);

            // Set dates
            LocalDate issueDate = LocalDate.now().minusDays(25 + i * 2);
            issued.setIssueDate(issueDate);
            issued.setDueDate(issueDate.plusDays(14));

            // Set status and return date - More overdue books for fines
            if (i % 4 == 0) {
                // 25% books are returned
                issued.setStatus("RETURNED");
                issued.setReturnDate(LocalDate.now().minusDays(i));
                issued.setFineAmount(BigDecimal.ZERO);
            } else if (i % 4 == 1 || i % 4 == 2) {
                // 50% books are overdue (for fine generation)
                issued.setStatus("OVERDUE");
                issued.setReturnDate(null);
                long daysOverdue = LocalDate.now().toEpochDay() - issued.getDueDate().toEpochDay();
                issued.setFineAmount(new BigDecimal(daysOverdue * 5)); // Rs. 5 per day
            } else {
                // 25% books are still borrowed but not overdue
                issued.setStatus("BORROWED");
                issued.setReturnDate(null);
                issued.setFineAmount(BigDecimal.ZERO);
            }

            issuedBooks.add(issuedBooksRepo.save(issued));
            userIndex++;
        }

        return issuedBooks;
    }

    private void createFines(List<User> users, List<IssuedBooks> issuedBooks) {
        // Create fines for overdue books
        List<IssuedBooks> overdueBooks = issuedBooks.stream()
                .filter(ib -> "OVERDUE".equals(ib.getStatus()))
                .toList();

        int fineCounter = 0;
        for (IssuedBooks issuedBook : overdueBooks) {
            Fine fine = new Fine();
            fine.setUser(issuedBook.getUser());
            fine.setIssuedBook(issuedBook);
            fine.setAmount(issuedBook.getFineAmount());
            fine.setCreatedAt(LocalDate.now().minusDays(10 - (fineCounter % 5)));

            // 40% of fines are paid, 60% are unpaid
            if (fineCounter % 5 < 2) {
                fine.setPaid(true);
                fine.setPaidAt(LocalDate.now().minusDays(3 - (fineCounter % 3)));

                // Update user's total unpaid fine to 0 for paid fines
                // (Fine is paid, so remove from total unpaid)
            } else {
                fine.setPaid(false);
                fine.setPaidAt(null);

                // Update user's total unpaid fine
                User user = issuedBook.getUser();
                user.setTotalUnpaidFine(user.getTotalUnpaidFine().add(fine.getAmount()));
                if (user.getTotalUnpaidFine().compareTo(new BigDecimal("100.00")) > 0) {
                    user.setDefaulter(true);
                }
                userRepo.save(user);
            }

            fineRepo.save(fine);
            fineCounter++;
        }

        // 2. Create fines for returned books that were returned late
        List<IssuedBooks> returnedBooks = issuedBooks.stream()
                .filter(ib -> "RETURNED".equals(ib.getStatus()))
                .toList();

        int lateReturnFines = 0;
        for (int i = 0; i < returnedBooks.size(); i++) {
            IssuedBooks returnedBook = returnedBooks.get(i);

            // Create late return fines for some returned books
            if (i % 2 == 0 && returnedBook.getReturnDate() != null) {
                LocalDate dueDate = returnedBook.getDueDate();
                LocalDate returnDate = returnedBook.getReturnDate();

                // Simulate that it was returned late (add days to return date)
                LocalDate simulatedReturnDate = returnDate.plusDays(i + 3);

                if (simulatedReturnDate.isAfter(dueDate)) {
                    long daysLate = simulatedReturnDate.toEpochDay() - dueDate.toEpochDay();
                    BigDecimal lateReturnFine = new BigDecimal(daysLate * 5); // Rs. 5 per day

                    if (lateReturnFine.compareTo(BigDecimal.ZERO) > 0) {
                        Fine fine = new Fine();
                        fine.setUser(returnedBook.getUser());
                        fine.setIssuedBook(returnedBook);
                        fine.setAmount(lateReturnFine);
                        fine.setCreatedAt(simulatedReturnDate);

                        // Most late return fines are paid
                        fine.setPaid(i % 3 != 0); // 67% paid
                        fine.setPaidAt(fine.isPaid() ? simulatedReturnDate.plusDays(1) : null);

                        if (!fine.isPaid()) {
                            User user = returnedBook.getUser();
                            user.setTotalUnpaidFine(user.getTotalUnpaidFine().add(fine.getAmount()));
                            if (user.getTotalUnpaidFine().compareTo(new BigDecimal("100.00")) > 0) {
                                user.setDefaulter(true);
                            }
                            userRepo.save(user);
                        }

                        fineRepo.save(fine);
                        fineCounter++;
                        lateReturnFines++;
                    }
                }
            }
        }

        // 3. Create some additional historical fines for variety
        int historicalFines = 0;
        for (int i = 0; i < Math.min(5, users.size()); i++) {
            User user = users.get(i);

            // Create a historical fine (already paid)
            Fine historicalFine = new Fine();
            historicalFine.setUser(user);

            // Link to the first issued book for this user if available
            IssuedBooks relatedBook = issuedBooks.stream()
                    .filter(ib -> ib.getUser().getId().equals(user.getId()))
                    .findFirst()
                    .orElse(issuedBooks.get(0)); // Fallback to first book

            historicalFine.setIssuedBook(relatedBook);
            historicalFine.setAmount(new BigDecimal((i + 1) * 25)); // Rs. 25, 50, 75, 100, 125
            historicalFine.setCreatedAt(LocalDate.now().minusDays(30 + i * 5));
            historicalFine.setPaid(true);
            historicalFine.setPaidAt(LocalDate.now().minusDays(25 + i * 5));

            fineRepo.save(historicalFine);
            fineCounter++;
            historicalFines++;
        }

        log.info("Created {} total fines: {} from overdue books, {} from late returns, {} historical fines",
                fineCounter, overdueBooks.size(), lateReturnFines, historicalFines);
    }

    private void createBookDonations(List<User> users) {
        String[][] donationData = {
                { "The Great Gatsby", "F. Scott Fitzgerald", "5", DonationStatus.PENDING.name(), "" },
                { "1984", "George Orwell", "3", DonationStatus.APPROVED.name(),
                        "Excellent condition, approved for library" },
                { "To Kill a Mockingbird", "Harper Lee", "4", DonationStatus.APPROVED.name(), "Well preserved copies" },
                { "The Catcher in the Rye", "J.D. Salinger", "2", DonationStatus.REJECTED.name(),
                        "Duplicate copies already available" },
                { "Animal Farm", "George Orwell", "6", DonationStatus.PENDING.name(), "" },
                { "Brave New World", "Aldous Huxley", "3", DonationStatus.APPROVED.name(), "Approved, good addition" },
                { "Lord of the Flies", "William Golding", "4", DonationStatus.PENDING.name(), "" },
                { "The Hobbit", "J.R.R. Tolkien", "10", DonationStatus.APPROVED.name(), "Popular book, approved" },
                { "Fahrenheit 451", "Ray Bradbury", "2", DonationStatus.REJECTED.name(), "Poor condition" },
                { "The Road", "Cormac McCarthy", "5", DonationStatus.PENDING.name(), "" },
                { "Life of Pi", "Yann Martel", "3", DonationStatus.APPROVED.name(), "Great donation" },
                { "The Kite Runner", "Khaled Hosseini", "4", DonationStatus.PENDING.name(), "" }
        };

        for (int i = 0; i < donationData.length; i++) {
            String[] data = donationData[i];
            BookDonation donation = new BookDonation();

            // Assign to a regular user
            donation.setUser(users.get(i % 15));
            donation.setBookTitle(data[0]);
            donation.setAuthor(data[1]);
            donation.setQuantityOffered(Integer.parseInt(data[2]));
            donation.setStatus(DonationStatus.valueOf(data[3]));
            donation.setCreatedAt(LocalDate.now().minusDays(15 + i));

            if (donation.getStatus() == DonationStatus.APPROVED) {
                donation.setQuantityApproved(Integer.parseInt(data[2]));
                donation.setAdminNotes(data[4]);
                donation.setProcessedAt(LocalDate.now().minusDays(10 + i));
            } else if (donation.getStatus() == DonationStatus.REJECTED) {
                donation.setQuantityApproved(0);
                donation.setAdminNotes(data[4]);
                donation.setProcessedAt(LocalDate.now().minusDays(10 + i));
            } else {
                donation.setQuantityApproved(0);
                donation.setAdminNotes(data[4]);
                donation.setProcessedAt(null);
            }

            bookDonationRepo.save(donation);
        }
    }

    private void createComplaints(List<User> users) {
        Object[][] complaintData = {
                { "Book Damaged on Arrival", "The book I borrowed had torn pages.", ComplaintCategory.BOOK_DAMAGE,
                        ComplaintStatus.PENDING },
                { "Incorrect Fine Charged", "I was charged Rs. 100 but I returned the book on time.",
                        ComplaintCategory.FINE_DISPUTE, ComplaintStatus.PENDING },
                { "Staff Rude Behavior", "The staff member was very rude when I asked for help.",
                        ComplaintCategory.STAFF_BEHAVIOR, ComplaintStatus.PENDING },
                { "Unable to Borrow Book", "The system shows book available but I cannot borrow it.",
                        ComplaintCategory.BORROW_ISSUE, ComplaintStatus.PENDING },
                { "Website Login Issue", "Cannot login to my account, keeps showing error.",
                        ComplaintCategory.SYSTEM_ERROR, ComplaintStatus.PENDING },
                { "Reading Room Too Noisy", "The reading area needs better noise control.", ComplaintCategory.FACILITY,
                        ComplaintStatus.PENDING },
                { "Donation Not Processed", "My book donation from last month has not been reviewed.",
                        ComplaintCategory.DONATION_ISSUE, ComplaintStatus.PENDING },
                { "Book Return Not Recorded", "I returned a book but it still shows as borrowed.",
                        ComplaintCategory.SYSTEM_ERROR, ComplaintStatus.PENDING },
                { "Payment Gateway Failed", "Online payment for fine failed but amount deducted.",
                        ComplaintCategory.SYSTEM_ERROR, ComplaintStatus.PENDING },
                { "Missing Pages in Book", "The book has missing pages 50-60.", ComplaintCategory.BOOK_DAMAGE,
                        ComplaintStatus.PENDING },
                { "Late Fee Incorrect", "Late fee calculation seems wrong.", ComplaintCategory.FINE_DISPUTE,
                        ComplaintStatus.PENDING },
                { "Membership Issue", "Cannot renew my library membership online.", ComplaintCategory.OTHER,
                        ComplaintStatus.PENDING }
        };

        for (int i = 0; i < complaintData.length; i++) {
            Object[] data = complaintData[i];
            Complaint complaint = new Complaint();

            // Assign to a regular user
            complaint.setComplainant(users.get(i % 15));
            complaint.setSubject((String) data[0]);
            complaint.setDescription((String) data[1]);
            complaint.setCategory((ComplaintCategory) data[2]);
            complaint.setStatus((ComplaintStatus) data[3]);
            complaint.setFirstStaffRejected(false);
            complaint.setSecondStaffRejected(false);
            complaint.setCreatedAt(LocalDateTime.now().minusDays(20 + i));

            complaintRepo.save(complaint);
        }
    }
}
