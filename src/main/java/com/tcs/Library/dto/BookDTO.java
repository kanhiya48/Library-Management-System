
package com.tcs.Library.dto;

import java.util.Set;
import com.tcs.Library.enums.BookType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookDTO {
    private String bookTitle;
    private BookType category;
    private String coverUrl;
    private int quantity;
    private Set<Long> authorId;
    private String authorName;
    private String authorEmail;
}
