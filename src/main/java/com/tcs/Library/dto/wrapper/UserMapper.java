
package com.tcs.Library.dto.wrapper;

import com.tcs.Library.dto.UserRegRequest;
import com.tcs.Library.entity.*;

public class UserMapper {
    private UserMapper() {
    }

    public static User toEntity(UserRegRequest dto) {
        if (dto == null)
            return null;

        User user = new User();

        user.setCustomerName(dto.getCustomerName());
        user.setEmail(dto.getEmail());
        user.setCountryCode(dto.getCountryCode());
        user.setMobileNumber(dto.getMobileNumber());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());
        // Password will be hashed separately in AuthService
        user.setSecretQuestion(dto.getSecretQuestion());
        // SecretAnswer will be hashed separately in AuthService

        return user;
    }
}
