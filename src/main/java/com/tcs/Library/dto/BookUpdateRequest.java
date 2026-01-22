package com.tcs.Library.dto;

import com.tcs.Library.enums.BookType;
import lombok.Data;

@Data
public class BookUpdateRequest {
    private String title;
    private String author;
    private BookType category;
    private String description;
}
