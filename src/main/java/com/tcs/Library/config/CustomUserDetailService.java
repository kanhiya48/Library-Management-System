package com.tcs.Library.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.tcs.Library.enums.Role;
import com.tcs.Library.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepo usr;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.tcs.Library.entity.User user = usr.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPasswordHash(), user.getAuthorities());
    }

}
