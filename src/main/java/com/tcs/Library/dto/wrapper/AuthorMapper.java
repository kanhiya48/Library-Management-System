package com.tcs.Library.dto.wrapper;

import com.tcs.Library.dto.AuthorSignUp;
import com.tcs.Library.entity.Author;

public class AuthorMapper {
    private AuthorMapper() {
    }

    public static Author toEntity(AuthorSignUp dto) {
        if (dto == null)
            return null;
        Author author = new Author();
        author.setName(dto.getName());
        author.setEmail(dto.getEmail());
        return author;
    }
}
