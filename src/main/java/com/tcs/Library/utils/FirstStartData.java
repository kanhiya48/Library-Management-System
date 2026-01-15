package com.tcs.Library.utils;

import java.util.HashSet;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.Role;
import com.tcs.Library.repository.UserRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstStartData {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createSystemUsersOnStart() {
        // Create Admin
        createUserIfNotExists(
                "admin@library.com",
                "admin@123",
                "System Admin",
                Set.of(Role.ADMIN));

        // Create Staff 1
        createUserIfNotExists(
                "staff1@library.com",
                "staff@123",
                "Staff Member 1",
                Set.of(Role.STAFF));

        // Create Staff 2
        createUserIfNotExists(
                "staff2@library.com",
                "staff@123",
                "Staff Member 2",
                Set.of(Role.STAFF));

        log.info("System users initialized: 1 Admin, 2 Staff");
    }

    private void createUserIfNotExists(String email, String password, String name, Set<Role> roles) {
        if (!userRepo.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setCustomerName(name);
            user.setRoles(new HashSet<>(roles));
            userRepo.save(user);
            log.info("Created system user: {} with roles: {}", email, roles);
        }
    }
}
