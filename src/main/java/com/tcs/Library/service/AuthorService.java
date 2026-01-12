package com.tcs.Library.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.Library.dto.AuthorSignUp;
import com.tcs.Library.dto.wrapper.AuthorMapper;
import com.tcs.Library.entity.Author;
import com.tcs.Library.entity.Book;
import com.tcs.Library.error.NoAuthorFoundException;
import com.tcs.Library.repository.AuthorRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepo authorRepo;

    @Transactional(readOnly = true)
    public List<Book> getBooksByAuthorId(Long authorId) {
        Author author = authorRepo.findById(authorId)
                .orElseThrow(() -> new NoAuthorFoundException(
                        "No author found with id: " + authorId));

        return author.getBook();
    }

    @Transactional
    public Author registerAuthor(AuthorSignUp dto) {
        Author author = AuthorMapper.toEntity(dto);
        return authorRepo.save(author);
    }
}
