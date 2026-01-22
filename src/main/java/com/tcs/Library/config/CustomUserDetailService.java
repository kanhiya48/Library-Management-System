package com.tcs.Library.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.tcs.Library.repository.UserRepo;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepo usr;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.tcs.Library.entity.User user = usr.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

}
