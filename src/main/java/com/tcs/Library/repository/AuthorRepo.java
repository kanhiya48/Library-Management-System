package com.tcs.Library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.Library.entity.Author;

@Repository
public interface AuthorRepo extends JpaRepository<Author, Long> {

    /** Find author by unique email */
    Optional<Author> findByEmail(String email);

    /** Check if author email already exists */
    boolean existsByEmail(String email);

    /** Find authors by name containing (case insensitive) */
    List<Author> findByNameContainingIgnoreCase(String name);

    /** Paginated search by name */
    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /** Find all authors with pagination */
    Page<Author> findAll(Pageable pageable);
}
