package com.tcs.Library.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.tcs.Library.enums.BookType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@Entity
@Table(name = "book")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private String publicId;

    @Column(name = "book_title", nullable = false)
    private String bookTitle;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private BookType category;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "total_copies")
    private int totalCopies;

    @ManyToMany
    @JoinTable(name = "author_book", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @JsonIgnore
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BookCopy> copies = new ArrayList<>();

    @PrePersist
    public void generatePublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    /**
     * Returns the author names as a comma-separated string.
     * This is a computed property for frontend display.
     */
    @JsonProperty("author")
    public String getAuthor() {
        if (authors == null || authors.isEmpty()) {
            return "Unknown";
        }
        return authors.stream()
                .map(Author::getName)
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.joining(", "));
    }
}
