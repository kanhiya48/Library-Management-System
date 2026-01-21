package com.tcs.Library.repository;

import com.tcs.Library.entity.User;
import com.tcs.Library.enums.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByPublicId(UUID publicId);

    boolean existsByEmail(String email);

    // For dashboard stats - count users having a specific role
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") Role role);

    long countByIsDefaulter(boolean isDefaulter);

    // For admin user management - find users having a specific role
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r = :role")
    Page<User> findByRole(@Param("role") Role role, Pageable pageable);

    Page<User> findByCustomerNameContainingIgnoreCase(String name, Pageable pageable);
}
