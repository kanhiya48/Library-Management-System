package com.tcs.Library.service;

import org.springframework.stereotype.Service;
import com.tcs.Library.repository.UserRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UniqueCheckServiceImpl implements UniqueCheckService {
    private final UserRepo userDS;

    public boolean isEmailRegistered(String email) {
        if (userDS.existsByEmail(email))
            return true;
        return false;
    }

    public boolean isMobileRegistered(String countryCode, String mobileNumber) {
        if (userDS.existsByCountryCodeAndMobileNumber(countryCode, mobileNumber))
            return true;
        return false;
    }

}
