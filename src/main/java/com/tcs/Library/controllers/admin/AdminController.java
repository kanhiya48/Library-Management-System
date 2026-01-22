package com.tcs.Library.controllers.admin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tcs.Library.dto.ApiResponse;
import com.tcs.Library.dto.DashboardStats;
import com.tcs.Library.dto.PagedResponse;
import com.tcs.Library.dto.BookCreateRequest;
import com.tcs.Library.dto.BookDTO;
import com.tcs.Library.dto.DonationApprovalRequest;
import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.BookDonation;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.Role;
import com.tcs.Library.error.NoUserFoundException;
import com.tcs.Library.repository.UserRepo;
import com.tcs.Library.service.BookService;
import com.tcs.Library.service.DashboardService;
import com.tcs.Library.service.DonationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.tcs.Library.dto.UserRegRequest;
import com.tcs.Library.dto.wrapper.UserMapper;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
public class AdminController {

    private final DonationService donationService;
    private final BookService bookService;
    private final DashboardService dashboardService;
    private final UserRepo userRepo;
    private final com.tcs.Library.service.BorrowService borrowService;
    private final PasswordEncoder passwordEncoder;

    // ========== DASHBOARD ==========

    /**
     * Get full dashboard statistics.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboardStats() {
        DashboardStats stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics", stats));
    }

    /**
     * Get quick summary stats.
     */
    @GetMapping("/dashboard/quick")
    public ResponseEntity<ApiResponse<DashboardStats>> getQuickStats() {
        DashboardStats stats = dashboardService.getQuickStats();
        return ResponseEntity.ok(ApiResponse.success("Quick statistics", stats));
    }

    // ========== USER MANAGEMENT ==========

    /**
     * Get all users with pagination.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PagedResponse<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "customerName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = userRepo.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", PagedResponse.from(users)));
    }

    /**
     * Get users by role with pagination.
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<PagedResponse<User>>> getUsersByRole(
            @PathVariable String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Role userRole = Role.valueOf(role.toUpperCase());
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepo.findByRole(userRole, pageable);
        return ResponseEntity.ok(ApiResponse.success("Users with role " + role, PagedResponse.from(users)));
    }

    /**
     * Search users by name.
     */
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<PagedResponse<User>>> searchUsers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepo.findByCustomerNameContainingIgnoreCase(name, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results", PagedResponse.from(users)));
    }

    /**
     * Get user by publicId.
     */
    @GetMapping("/users/{publicId}")
    public ResponseEntity<ApiResponse<User>> getUserByPublicId(@PathVariable String publicId) {
        User user = userRepo.findByPublicId(UUID.fromString(publicId))
                .orElseThrow(() -> new NoUserFoundException("User not found: " + publicId));
        return ResponseEntity.ok(ApiResponse.success("User found", user));
    }

    /**
     * Update user role.
     */
    @PutMapping("/users/{publicId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUserRole(
            @PathVariable String publicId,
            @RequestParam String role) {

        User user = userRepo.findByPublicId(UUID.fromString(publicId))
                .orElseThrow(() -> new NoUserFoundException("User not found: " + publicId));

        Role newRole = Role.valueOf(role.toUpperCase());
        Set<Role> roles = new HashSet<>();
        roles.add(newRole);
        user.setRoles(roles);
        userRepo.save(user);

        return ResponseEntity.ok(ApiResponse.success("User role updated to " + role, user));
    }

    /**
     * Toggle user defaulter status.
     */
    @PutMapping("/users/{publicId}/defaulter")
    public ResponseEntity<ApiResponse<User>> toggleDefaulterStatus(
            @PathVariable String publicId,
            @RequestParam boolean isDefaulter) {

        User user = userRepo.findByPublicId(UUID.fromString(publicId))
                .orElseThrow(() -> new NoUserFoundException("User not found: " + publicId));

        user.setDefaulter(isDefaulter);
        userRepo.save(user);

        String message = isDefaulter ? "User marked as defaulter" : "User removed from defaulter list";
        return ResponseEntity.ok(ApiResponse.success(message, user));
    }

    /**
     * Delete user by public ID.
     */
    @DeleteMapping("/users/{publicId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID publicId) {
        User user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new NoUserFoundException("User not found with ID: " + publicId));

        if (borrowService.hasActiveBorrowsForUser(user.getId())) {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Cannot delete user with active borrowed books. Please ensure all books are returned first."));
        }

        userRepo.delete(user);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    /**
     * Add new user/member.
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> addUser(@Valid @RequestBody UserRegRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Email already registered"));
        }

        User user = UserMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        // Admin created users are default USER role
        user.getRoles().add(Role.USER);

        User savedUser = userRepo.save(user);
        return ResponseEntity.ok(ApiResponse.success("User created successfully", savedUser));
    }

    // ========== DONATION MANAGEMENT ==========

    @GetMapping("/donations")
    public ResponseEntity<ApiResponse<List<BookDonation>>> getPendingDonations() {
        List<BookDonation> donations = donationService.getPendingDonations();
        return ResponseEntity.ok(ApiResponse.success("Pending donations retrieved", donations));
    }

    @GetMapping("/donations/all")
    public ResponseEntity<ApiResponse<PagedResponse<BookDonation>>> getAllDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookDonation> donations = donationService.getAllDonations(pageable);
        return ResponseEntity.ok(ApiResponse.success("Donation history retrieved", PagedResponse.from(donations)));
    }

    @PostMapping("/donations/{donationId}/approve")
    public ResponseEntity<ApiResponse<BookDonation>> approveDonation(
            @PathVariable Long donationId,
            @RequestBody DonationApprovalRequest request) {
        BookDonation approved = donationService.approveDonation(donationId, request);
        return ResponseEntity.ok(ApiResponse.success("Donation approved", approved));
    }

    @PostMapping("/donations/{donationId}/reject")
    public ResponseEntity<ApiResponse<BookDonation>> rejectDonation(
            @PathVariable Long donationId,
            @RequestBody(required = false) String adminNotes) {
        BookDonation rejected = donationService.rejectDonation(donationId, adminNotes);
        return ResponseEntity.ok(ApiResponse.success("Donation rejected", rejected));
    }

    // ========== BOOK MANAGEMENT ==========

    /**
     * Add a new book using author emails.
     * Authors will be looked up by email or created if they don't exist.
     */
    @PostMapping("/books")
    public ResponseEntity<ApiResponse<Book>> addBookWithAuthors(
            @Valid @RequestBody BookCreateRequest request) {
        Book savedBook = bookService.createBookWithAuthors(request);
        return ResponseEntity.ok(ApiResponse.success(
                "Book added with " + request.getQuantity() + " copies", savedBook));
    }

    /**
     * Add a new book using author IDs (legacy endpoint).
     */
    @PostMapping("/books/by-id")
    public ResponseEntity<ApiResponse<Book>> addBookById(@RequestBody BookDTO dto) {
        Book savedBook = bookService.createBook(dto);
        return ResponseEntity.ok(ApiResponse.success(
                "Book added with " + dto.getQuantity() + " copies", savedBook));
    }

    /**
     * Get all books with pagination.
     */
    @GetMapping("/books")
    public ResponseEntity<ApiResponse<PagedResponse<Book>>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bookTitle") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success("Books retrieved", PagedResponse.from(books)));
    }

    // ========== BOOK EXTENSIONS ==========

    @GetMapping("/books/{bookId}/borrowers")
    public ResponseEntity<ApiResponse<List<com.tcs.Library.entity.IssuedBooks>>> getBookBorrowers(
            @PathVariable Long bookId) {
        List<com.tcs.Library.entity.IssuedBooks> borrowers = borrowService.getActiveBorrowsForBook(bookId);
        return ResponseEntity.ok(ApiResponse.success("Active borrowers retrieved", borrowers));
    }

    @GetMapping("/books/{bookId}/can-edit")
    public ResponseEntity<ApiResponse<Boolean>> canEditBook(@PathVariable Long bookId) {
        boolean hasActiveBorrows = borrowService.hasActiveBorrows(bookId);
        return ResponseEntity.ok(ApiResponse.success("Check result", !hasActiveBorrows));
    }

    @PutMapping("/books/{bookId}")
    public ResponseEntity<ApiResponse<Book>> updateBook(
            @PathVariable Long bookId,
            @Valid @RequestBody com.tcs.Library.dto.BookUpdateRequest request) {
        Book updatedBook = bookService.updateBook(bookId, request);
        return ResponseEntity.ok(ApiResponse.success("Book details updated successfully", updatedBook));
    }

    /**
     * Delete a book by ID.
     * Book cannot be deleted if it has active borrows.
     */
    @DeleteMapping("/books/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteBook(@PathVariable Long bookId) {
        if (borrowService.hasActiveBorrows(bookId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error(
                    "Cannot delete book with active borrows. Please ensure all copies are returned first."));
        }
        bookService.deleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }

    // ========== BORROWED BOOKS ==========

    /**
     * Get all borrowed books with pagination.
     * Returns all borrow records (borrowed, overdue, and returned).
     */
    @GetMapping("/borrowed-books")
    public ResponseEntity<ApiResponse<PagedResponse<com.tcs.Library.entity.IssuedBooks>>> getAllBorrowedBooks(
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) java.time.LocalDate startDate,
            @RequestParam(required = false) java.time.LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "issueDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<com.tcs.Library.entity.IssuedBooks> borrowedBooks = borrowService.getAllBorrowedBooksWithFilters(
                memberName, category, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success("Borrowed books retrieved", PagedResponse.from(borrowedBooks)));
    }

    /**
     * Get currently active borrowed books (not returned).
     */
    @GetMapping("/borrowed-books/active")
    public ResponseEntity<ApiResponse<PagedResponse<com.tcs.Library.entity.IssuedBooks>>> getActiveBorrowedBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "issueDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<com.tcs.Library.entity.IssuedBooks> borrowedBooks = borrowService.getAllActiveBorrowedBooks(pageable);
        return ResponseEntity
                .ok(ApiResponse.success("Active borrowed books retrieved", PagedResponse.from(borrowedBooks)));
    }
}
