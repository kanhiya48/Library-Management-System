package com.tcs.Library.entity;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
public class Author {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long Id;
    @Column(name = "author_name")
    private String name;
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    private String publicId;

    @JsonIgnore
    @ManyToMany(mappedBy = "authors")
    private List<Book> book;

    @PrePersist
    public void generatePublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    /**
     * Returns the count of books associated with this author.
     * This is a computed property that is included in JSON responses.
     */
    @JsonProperty("bookCount")
    public int getBookCount() {
        return book != null ? book.size() : 0;
    }
}
