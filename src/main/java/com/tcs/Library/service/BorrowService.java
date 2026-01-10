package com.tcs.Library.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.BookCopy;
import com.tcs.Library.entity.User;
import com.tcs.Library.error.NoUserFoundException;
import com.tcs.Library.repository.BookRepo;
import com.tcs.Library.repository.UserRepo;

public class BorrowService {
    @Autowired
    private BookService booksvc;
    @Autowired
    private UserRepo userRepo;


    public BookCopy issueBook(String bookPubId, Long usrId, String usrPubId) {
        Book book = booksvc.getBookByPubId(bookPubId);
        User user = userRepo.findById(usrId).orElseThrow(() -> new NoUserFoundException(usrPubId));
        Set<Long> books = user.getBooks();
        Long bookId=book.getId();
       // List<Long> unique = new HashSet<>();
        if(books.contains(bookId)){
            
        }



    }

}
