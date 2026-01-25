package com.tcs.Library.service;

import com.tcs.Library.dto.DonationApprovalRequest;
import com.tcs.Library.dto.DonationRequest;
import com.tcs.Library.dto.DonationResponse;
import com.tcs.Library.entity.Author;
import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.BookCopy;
import com.tcs.Library.entity.BookDonation;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.BookStatus;
import com.tcs.Library.enums.DonationStatus;
import com.tcs.Library.error.NoUserFoundException;
import com.tcs.Library.repository.AuthorRepo;
import com.tcs.Library.repository.BookCopyRepo;
import com.tcs.Library.repository.BookDonationRepo;
import com.tcs.Library.repository.BookRepo;
import com.tcs.Library.repository.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonationService {

    private final BookDonationRepo donationRepo;
    private final BookRepo bookRepo;
    private final BookCopyRepo bookCopyRepo;
    private final UserRepo userRepo;
    private final AuthorRepo authorRepo;

    @Transactional
    public BookDonation submitDonation(UUID userPublicId, DonationRequest request) {
        User user = userRepo.findByPublicId(userPublicId)
                .orElseThrow(() -> new NoUserFoundException("User not found: " + userPublicId));

        BookDonation donation = new BookDonation();
        donation.setUser(user);
        donation.setBookTitle(request.getBookTitle());
        donation.setAuthor(request.getAuthor());
        donation.setQuantityOffered(request.getQuantityOffered());
        donation.setStatus(DonationStatus.PENDING);

        log.info("Donation submitted by user {}: {} copies of '{}'",
                user.getEmail(), request.getQuantityOffered(), request.getBookTitle());
        return donationRepo.save(donation);
    }

    public List<DonationResponse> getPendingDonations() {
        List<BookDonation> donations = donationRepo.findByStatus(DonationStatus.PENDING);
        return donations.stream()
                .map(d -> new DonationResponse(
                        d.getId(),
                        d.getBookTitle(),
                        d.getAuthor(),
                        d.getQuantityOffered(),
                        d.getQuantityApproved(),
                        d.getStatus(),
                        d.getAdminNotes(),
                        d.getCreatedAt(),
                        d.getProcessedAt()))
                .toList();
    }

    public List<DonationResponse> getUserDonations(UUID userPublicId) {
        User user = userRepo.findByPublicId(userPublicId)
                .orElseThrow(() -> new NoUserFoundException("User not found: " + userPublicId));
        List<BookDonation> donations = donationRepo.findByUserId(user.getId());
        return donations.stream()
                .map(d -> new DonationResponse(
                        d.getId(),
                        d.getBookTitle(),
                        d.getAuthor(),
                        d.getQuantityOffered(),
                        d.getQuantityApproved(),
                        d.getStatus(),
                        d.getAdminNotes(),
                        d.getCreatedAt(),
                        d.getProcessedAt()))
                .toList();
    }

    public Page<BookDonation> getAllDonations(Pageable pageable) {
        return donationRepo.findAll(pageable);
    }

    @Transactional
    public BookDonation approveDonation(Long donationId, DonationApprovalRequest request) {
        BookDonation donation = donationRepo.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found: " + donationId));

        if (donation.getStatus() != DonationStatus.PENDING) {
            throw new RuntimeException("Donation is not in pending status");
        }

        if (request.getQuantityApproved() > donation.getQuantityOffered()) {
            throw new RuntimeException("Cannot approve more than offered quantity");
        }

        donation.setQuantityApproved(request.getQuantityApproved());
        donation.setAdminNotes(request.getAdminNotes());
        donation.setStatus(DonationStatus.APPROVED);
        donation.setProcessedAt(LocalDate.now());
        donationRepo.save(donation);

        // Create book and copies if quantity > 0
        if (request.getQuantityApproved() > 0) {
            Book book = bookRepo.findByBookTitleIgnoreCase(donation.getBookTitle())
                    .orElseGet(() -> {
                        Book newBook = new Book();
                        newBook.setBookTitle(donation.getBookTitle());
                        newBook.setTotalCopies(0);
                        return bookRepo.save(newBook);
                    });

            // Handle Author mapping
            if (donation.getAuthor() != null && !donation.getAuthor().trim().isEmpty()) {
                String authorName = donation.getAuthor().trim();
                Author author = authorRepo.findByNameIgnoreCase(authorName)
                        .orElseGet(() -> {
                            Author newAuthor = new Author();
                            newAuthor.setName(authorName);
                            return authorRepo.save(newAuthor);
                        });

                if (book.getAuthors() == null) {
                    book.setAuthors(new java.util.HashSet<>());
                }
                book.getAuthors().add(author);
                bookRepo.save(book);
            }

            // Add copies
            for (int i = 0; i < request.getQuantityApproved(); i++) {
                BookCopy copy = new BookCopy();
                copy.setBook(book);
                copy.setStatus(BookStatus.AVAILABLE);
                bookCopyRepo.save(copy);
            }

            // Update total copies count
            book.setTotalCopies(book.getTotalCopies() + request.getQuantityApproved());
            bookRepo.save(book);

            log.info("Created {} copies for book '{}' from donation",
                    request.getQuantityApproved(), book.getBookTitle());
        }

        log.info("Donation {} approved: {} of {} copies accepted",
                donationId, request.getQuantityApproved(), donation.getQuantityOffered());
        return donation;
    }

    @Transactional
    public BookDonation rejectDonation(Long donationId, String adminNotes) {
        BookDonation donation = donationRepo.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found: " + donationId));

        if (donation.getStatus() != DonationStatus.PENDING) {
            throw new RuntimeException("Donation is not in pending status");
        }

        donation.setStatus(DonationStatus.REJECTED);
        donation.setAdminNotes(adminNotes);
        donation.setProcessedAt(LocalDate.now());

        log.info("Donation {} rejected", donationId);
        return donationRepo.save(donation);
    }
}
