package com.tcs.Library.dto.wrapper;

import java.util.HashSet;
import java.util.Set;

import com.tcs.Library.dto.BookDTO;
import com.tcs.Library.entity.Author;
import com.tcs.Library.entity.Book;

public class BookMapper {

    private BookMapper() {
    }

    public static Book toEntity(BookDTO dto, Set<Author> authors) {
        Book book = new Book();
        book.setBookTitle(dto.getBookTitle());
        book.setCategory(dto.getCategory());
        book.setCoverUrl(dto.getCoverUrl());
        book.setTotalCopies(dto.getQuantity());
        book.setAuthors(authors == null ? new HashSet<>() : authors);

        // Map new fields
        book.setAuthorName(dto.getAuthorName());
        book.setAuthorEmail(dto.getAuthorEmail());

        // Fallback for authorName if null (since it's mandatory)
        if (book.getAuthorName() == null && authors != null && !authors.isEmpty()) {
            book.setAuthorName(authors.iterator().next().getName());
        }
        // If still null, it might fail validation, but we leave it to database or
        // caller checking.
        return book;
    }
}
