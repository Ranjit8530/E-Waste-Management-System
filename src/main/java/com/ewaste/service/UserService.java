package com.ewaste.service;

import com.ewaste.dto.AdminActionResponse;
import com.ewaste.entity.AccountStatus;
import com.ewaste.entity.User;
import com.ewaste.entity.UserRole;
import com.ewaste.exception.BadRequestException;
import com.ewaste.exception.ResourceNotFoundException;
import com.ewaste.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Page<User> listByRole(UserRole role, AccountStatus status, Pageable pageable) {
        if (status == null) {
            return userRepository.findByRole(role, pageable);
        }
        return userRepository.findByRoleAndStatus(role, status, pageable);
    }

    public Page<User> listAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public AdminActionResponse approveLocalAdmin(Long id, String location) {
        User admin = getLocalAdmin(id);
        if (location == null || location.isBlank()) {
            throw new BadRequestException("Location is required for approval");
        }
        admin.setStatus(AccountStatus.ACTIVE);
        admin.setLocation(location.trim());
        userRepository.save(admin);
        return new AdminActionResponse(admin.getId(), admin.getStatus().name(), admin.getLocation(), "Local admin approved");
    }

    public AdminActionResponse rejectLocalAdmin(Long id) {
        User admin = getLocalAdmin(id);
        admin.setStatus(AccountStatus.REJECTED);
        userRepository.save(admin);
        return new AdminActionResponse(admin.getId(), admin.getStatus().name(), admin.getLocation(), "Local admin rejected");
    }

    private User getLocalAdmin(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        if (user.getRole() != UserRole.LOCAL_ADMIN) {
            throw new BadRequestException("User is not a local admin account");
        }
        return user;
    }
}
