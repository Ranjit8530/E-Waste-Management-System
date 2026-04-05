package com.ewaste.repository;

import com.ewaste.entity.AccountStatus;
import com.ewaste.entity.User;
import com.ewaste.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    Page<User> findByRole(UserRole role, Pageable pageable);
    Page<User> findByRoleAndStatus(UserRole role, AccountStatus status, Pageable pageable);
}
