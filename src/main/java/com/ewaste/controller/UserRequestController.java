package com.ewaste.controller;

import com.ewaste.dto.PickupRequestCreateDto;
import com.ewaste.dto.PickupRequestResponseDto;
import com.ewaste.entity.User;
import com.ewaste.entity.UserRole;
import com.ewaste.service.AuthService;
import com.ewaste.service.PickupRequestService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserRequestController {
    private final AuthService authService;
    private final PickupRequestService pickupRequestService;

    @PostMapping("/requests")
    public ResponseEntity<PickupRequestResponseDto> createRequest(@Valid @RequestBody PickupRequestCreateDto dto,
                                                                  HttpSession session) {
        authService.ensureRole(session, UserRole.USER);
        User current = authService.getCurrentUser(session);
        return ResponseEntity.ok(PickupRequestResponseDto.fromEntity(pickupRequestService.createRequest(current, dto)));
    }

    @GetMapping("/user/requests")
    public ResponseEntity<Page<PickupRequestResponseDto>> getUserRequests(HttpSession session,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        authService.ensureRole(session, UserRole.USER);
        User current = authService.getCurrentUser(session);
        Page<PickupRequestResponseDto> response = pickupRequestService
                .getUserRequests(current.getId(), PageRequest.of(page, size))
                .map(PickupRequestResponseDto::fromEntity);
        return ResponseEntity.ok(response);
    }
}
