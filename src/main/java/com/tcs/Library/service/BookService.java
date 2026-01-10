
package com.tcs.Library.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.Library.dto.BookDTO;
import com.tcs.Library.dto.wrapper.BookMapper;
import com.tcs.Library.entity.Author;
import com.tcs.Library.entity.Book;
import com.tcs.Library.error.BookNotFoundException;
import com.tcs.Library.error.NoAuthorFoundException;
import com.tcs.Library.repository.AuthorRepo;
import com.tcs.Library.repository.BookRepo;

@Service
public class BookService {
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private AuthorRepo authorRepo;

    @Transactional
    public Book createBook(BookDTO dto) throws NoAuthorFoundException {

        Set<Long> aids = dto.getAuthorId();

        if (aids == null || aids.isEmpty()) {
            throw new NoAuthorFoundException("Author IDs are missing");
        }

        Set<Author> authorSet = new HashSet<>();
        authorRepo.findAllById(aids).forEach(authorSet::add);

        if (authorSet.size() != aids.size()) {
            throw new NoAuthorFoundException("One or more author IDs are invalid");
        }

        Book newBook = BookMapper.toEntity(dto, authorSet);
        return bookRepo.save(newBook);
    }

    public List<Book> searchBooks(String keyString) {
        return bookRepo.findAll();
    }


    public Book getBookByPubId(String pubId) {
        return bookRepo.findByPublicId(pubId).orElseThrow(() -> new BookNotFoundException(pubId));
    }


}
