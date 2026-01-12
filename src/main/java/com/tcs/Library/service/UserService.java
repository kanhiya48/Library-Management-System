
package com.tcs.Library.service;

import com.tcs.Library.dto.*;
import com.tcs.Library.utils.*;
import com.tcs.Library.utils.UserValidations.ValidationResult;
import com.tcs.Library.repository.*;

import java.time.Clock;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.tcs.Library.dto.wrapper.*;
import com.tcs.Library.entity.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userDS;
    private final UserValidations res;

    public void validateUser(UserRegRequest user) {

        ValidationResult result = res.validateRegistration(user, Clock.systemDefaultZone());

        if (result.isValid()) {
            User newUser = UserMapper.toEntity(user);
            System.out.print(newUser);
            userDS.save(newUser);
        } else {
            for (int i = 0; i < result.getErrors().size(); i++) {
                System.out.println(result.getErrors().get(i));
            }
            // System.out.println( result.getErrors());
        }
    }
}
