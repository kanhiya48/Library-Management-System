package com.tcs.Library.entity;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.tcs.Library.enums.BookStatus;
import com.tcs.Library.enums.BookType;
import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private String publicId;

    @Column(name = "book_title")
    private String book_title;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private BookType category;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "quantity")
    private int quantity;

    @ManyToMany
    @JoinTable(name = "author_book", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    private Set<Author> author = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = jakarta.persistence.CascadeType.ALL)
    private List<BookCopy> booksCopy = new ArrayList<>();

    @PrePersist
    public void generatePublicId() {
        if (publicId == null) {
            publicId = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }
    }
}
