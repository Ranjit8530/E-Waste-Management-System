package com.ewaste.controller;

import com.ewaste.dto.ApiMessage;
import com.ewaste.dto.AuthResponse;
import com.ewaste.dto.LoginRequest;
import com.ewaste.dto.RegisterRequest;
import com.ewaste.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        return ResponseEntity.ok(authService.login(request, session));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiMessage> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(new ApiMessage("Logged out successfully"));
    }
}
