package com.ewaste.service;

import com.ewaste.dto.AuthResponse;
import com.ewaste.dto.LoginRequest;
import com.ewaste.dto.RegisterRequest;
import com.ewaste.entity.AccountStatus;
import com.ewaste.entity.User;
import com.ewaste.entity.UserRole;
import com.ewaste.exception.BadRequestException;
import com.ewaste.exception.UnauthorizedException;
import com.ewaste.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String SESSION_USER_ID = "USER_ID";
    private static final String SESSION_ROLE = "USER_ROLE";
    private static final String SESSION_LOCATION = "USER_LOCATION";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (request.getRole() == UserRole.SUPER_ADMIN) {
            throw new BadRequestException("Cannot self-register as super admin");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone().trim());
        user.setAddress(request.getAddress().trim());
        user.setRole(request.getRole());

        if (request.getRole() == UserRole.LOCAL_ADMIN) {
            if (request.getLocation() == null || request.getLocation().isBlank()) {
                throw new BadRequestException("Location is required for local admin application");
            }
            user.setStatus(AccountStatus.PENDING);
            user.setLocation(request.getLocation().trim());
        } else {
            user.setStatus(AccountStatus.ACTIVE);
            user.setLocation(null);
        }

        User saved = userRepository.save(user);
        String msg = saved.getRole() == UserRole.LOCAL_ADMIN
                ? "Admin application submitted and pending approval"
                : "Registration successful";
        return toAuthResponse(saved, msg);
    }

    public AuthResponse login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new UnauthorizedException("Account is " + user.getStatus() + ". Please contact super admin");
        }

        session.setAttribute(SESSION_USER_ID, user.getId());
        session.setAttribute(SESSION_ROLE, user.getRole().name());
        session.setAttribute(SESSION_LOCATION, user.getLocation());

        return toAuthResponse(user, "Login successful");
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public User getCurrentUser(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            throw new UnauthorizedException("Please login first");
        }
        return userRepository.findById((Long) userId)
                .orElseThrow(() -> new UnauthorizedException("Session user not found"));
    }

    public void ensureRole(HttpSession session, UserRole... roles) {
        Object role = session.getAttribute(SESSION_ROLE);
        if (role == null) {
            throw new UnauthorizedException("Please login first");
        }
        for (UserRole allowed : roles) {
            if (allowed.name().equals(role.toString())) {
                return;
            }
        }
        throw new UnauthorizedException("You are not authorized for this action");
    }

    private AuthResponse toAuthResponse(User user, String message) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name(),
                user.getStatus().name(), user.getLocation(), message);
    }
}
