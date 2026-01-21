package com.tcs.Library.dto;

import java.util.Set;

import com.tcs.Library.enums.BookType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating books using author emails.
 * This allows frontend to send author information by email (unique identifier)
 * rather than internal IDs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookCreateRequest {

    @NotBlank(message = "Book title is required")
    private String bookTitle;

    @NotNull(message = "Category is required")
    private BookType category;

    private String coverUrl;

    @Positive(message = "Quantity must be positive")
    private int quantity = 1;

    /**
     * Set of author emails. Authors will be looked up by email.
     * If an author doesn't exist, they will be created.
     */
    @NotNull(message = "At least one author email is required")
    private Set<AuthorInfo> authors;

    /**
     * Inner class to hold author information.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        @NotBlank(message = "Author email is required")
        private String email;

        /** Name is optional if author already exists */
        private String name;
    }
}
