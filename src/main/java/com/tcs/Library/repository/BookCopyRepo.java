package com.tcs.Library.repository;

import com.tcs.Library.entity.Book;
import com.tcs.Library.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookCopyRepo extends JpaRepository<BookCopy, Long> {

}
