package com.tcs.Library.repository;

import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

    // @Query(value = """
    // SELECT *
    // FROM book b
    // WHERE
    // lower(b.book_title) % lower(:q)
    // OR lower(b.author) % lower(:q)
    // """, nativeQuery = true)
    // Optional<Book> findByAut(String email);
    // @Query(

    // )
    // List<Book> fuzzySearchBooks(String query);
    Optional<Book> findByPublicId(String publicId);
}

