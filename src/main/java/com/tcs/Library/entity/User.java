package com.tcs.Library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.tcs.Library.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@Entity
@Table(name = "users")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "public_id", nullable = false, unique = true, updatable = false)
    private UUID publicId;

    private String customerName;

    @Column(unique = true, nullable = false)
    private String email;

    private String countryCode;
    private String mobileNumber;
    private String address;
    private LocalDate dateOfBirth;
    @JsonIgnore
    private String passwordHash;
    private String secretQuestion;
    @JsonIgnore
    private String secretAnswerHash; // Hashed for security

    @Column(nullable = false)
    private boolean isDefaulter = false;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalUnpaidFine = BigDecimal.ZERO;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "users_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void generatePublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Custom getter for isDefaulter (Lombok generates isIsDefaulter)
    public boolean isDefaulter() {
        return isDefaulter;
    }

    public void setDefaulter(boolean defaulter) {
        isDefaulter = defaulter;
    }
}
