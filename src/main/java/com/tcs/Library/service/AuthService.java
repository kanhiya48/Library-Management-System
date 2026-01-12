package com.tcs.Library.service;

import com.tcs.Library.config.CustomUserDetailService;
import org.springframework.security.core.Authentication;
import java.time.Clock;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.tcs.Library.dto.LoginRequest;
import com.tcs.Library.dto.LoginResponse;
import com.tcs.Library.dto.SignUpResponse;
import com.tcs.Library.dto.UserRegRequest;
import com.tcs.Library.dto.wrapper.UserMapper;
import com.tcs.Library.enums.Role;
import com.tcs.Library.repository.UserRepo;
import com.tcs.Library.utils.AuthUtils;
import com.tcs.Library.utils.UserValidations;
import com.tcs.Library.utils.UserValidations.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final AuthUtils authUtils;
    private final UserRepo userDS;
    private final UserValidations res;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService detailService;

    public LoginResponse login(LoginRequest req) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(req.getEmail(),
                req.getPassword());

        authManager.authenticate(authToken);

        UserDetails userDetails = detailService.loadUserByUsername(req.getEmail());

        String token = authUtils.generateAccessToken(userDetails.getUsername(),
                userDetails.getAuthorities());

        return new LoginResponse(token);
    }

    public SignUpResponse signup(UserRegRequest req) {
        ValidationResult result = res.validateRegistration(req, Clock.systemDefaultZone());
        if (result.isValid()) {
            com.tcs.Library.entity.User newUser = UserMapper.toEntity(req);
            newUser.setPasswordHash(passwordEncoder.encode(req.getPassword()));
            if (newUser.getRoles().isEmpty()) {
                newUser.getRoles().add(Role.USER);
            }
            userDS.save(newUser);
            return new SignUpResponse(newUser.getPublicId());
        }
        throw new IllegalArgumentException("User Already Exists");
    }
}
