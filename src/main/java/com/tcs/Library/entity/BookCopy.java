package com.tcs.Library.entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tcs.Library.enums.BookStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "book_copy")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "copy_public_id", unique = true)
    private String copyPublicId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_user_id")
    private User currentUser;

    @PrePersist
    public void generateCopyPublicId() {
        if (copyPublicId == null && book != null && book.getPublicId() != null) {
            copyPublicId = book.getPublicId() + "-" + UUID.randomUUID().toString();
        }
    }

    // Computed property for book title (for frontend display)
    @JsonProperty("bookTitle")
    public String getBookTitle() {
        return book != null ? book.getBookTitle() : null;
    }
}
