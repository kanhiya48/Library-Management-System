package com.tcs.Library.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.BookCopy;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.BookStatus;
import com.tcs.Library.error.BookNotFoundException;
import com.tcs.Library.error.NoUserFoundException;
import com.tcs.Library.error.SameBookException;
import com.tcs.Library.error.UserDefaulterException;
import com.tcs.Library.repository.BookCopyRepo;
import com.tcs.Library.repository.BookRepo;
import com.tcs.Library.repository.UserRepo;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final UserRepo userRepo;
    private final BookRepo bookRepo;
    private final BookCopyRepo bookCopyRepo;

    @Transactional
    public com.tcs.Library.dto.IssueBookResponseDTO issueBook(String bookPubId, Long usrId, String usrPubId)
            throws SameBookException {

        Book book = bookRepo.findByPublicId(bookPubId)
                .orElseThrow(() -> new BookNotFoundException(bookPubId));
        User user = userRepo.findById(usrId)
                .orElseThrow(() -> new NoUserFoundException(usrPubId));
        if (user.isDefaulter()) {
            throw new UserDefaulterException("User is a defaulter");
        }
        for (BookCopy bc : user.getBookCopy()) {
            if (bc.getReturnTime() != null &&
                    LocalDateTime.now().isAfter(bc.getReturnTime()) || bc.getStatus().equals(BookStatus.OVERDUE)) {
                user.setDefaulter(true);
                userRepo.save(user);
                throw new UserDefaulterException(user.getPublicId().toString());
            }
            String currentBookPubId = bc.getCopypubId().split("_")[0];
            if (currentBookPubId.equals(bookPubId)) {
                throw new SameBookException(bookPubId);
            }
        }
        BookCopy availableCopy = bookCopyRepo
                .findFirstByBookAndStatusIn(book, List.of(BookStatus.RETURNED, BookStatus.FIRST))
                .orElseThrow(() -> new RuntimeException("No available copies for book: " + bookPubId));

        availableCopy.setStatus(BookStatus.BORROWED);
        availableCopy.setUser(user);
        availableCopy.setIssueTime(LocalDateTime.now());
        availableCopy.setReturnTime(LocalDateTime.now().plusDays(14));
        user.getBookCopy().add(availableCopy);

        return com.tcs.Library.dto.IssueBookResponseDTO.builder()
                .bookTitle(book.getBook_title())
                .bookCopyPublicId(availableCopy.getCopypubId())
                .userPublicId(user.getPublicId().toString())
                .userName(user.getCustomerName())
                .issueTime(availableCopy.getIssueTime())
                .returnTime(availableCopy.getReturnTime())
                .status(availableCopy.getStatus().name())
                .build();
    }
}
