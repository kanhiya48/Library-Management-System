package com.tcs.Library.utils;

import java.util.HashSet;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.tcs.Library.entity.User;
import com.tcs.Library.enums.Role;
import com.tcs.Library.repository.UserRepo;
import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class FirstStartData {
    private final UserRepo userDS;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdminonStart() {
        if (!userDS.existsByEmail("sher@system.com")) {
            User admin = new User();
            admin.setEmail("sher@system.com");
            admin.setPasswordHash(passwordEncoder.encode("sher@123"));
            Set<Role> st = new HashSet<Role>();
            st.add(Role.ADMIN);
            admin.setRoles(st);
            userDS.save(admin);
        }
    }
}
