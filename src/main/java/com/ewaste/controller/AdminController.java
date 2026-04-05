package com.ewaste.controller;

import com.ewaste.dto.AdminAnalyticsDto;
import com.ewaste.dto.PickupRequestResponseDto;
import com.ewaste.dto.UpdateStatusRequest;
import com.ewaste.entity.RequestStatus;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AuthService authService;
    private final PickupRequestService pickupRequestService;

    @GetMapping("/requests")
    public ResponseEntity<Page<PickupRequestResponseDto>> getLocationRequests(HttpSession session,
                                                                              @RequestParam(required = false) RequestStatus status,
                                                                              @RequestParam(required = false) String keyword,
                                                                              @RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size) {
        authService.ensureRole(session, UserRole.LOCAL_ADMIN);
        User current = authService.getCurrentUser(session);
        Page<PickupRequestResponseDto> response = pickupRequestService
                .getRequestsForLocalAdmin(current, status, keyword, PageRequest.of(page, size))
                .map(PickupRequestResponseDto::fromEntity);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/request/{id}/status")
    public ResponseEntity<PickupRequestResponseDto> updateRequestStatus(@PathVariable Long id,
                                                                        @Valid @RequestBody UpdateStatusRequest request,
                                                                        HttpSession session) {
        authService.ensureRole(session, UserRole.LOCAL_ADMIN);
        User current = authService.getCurrentUser(session);
        return ResponseEntity.ok(PickupRequestResponseDto.fromEntity(
                pickupRequestService.updateStatus(current, id, request, false)));
    }

    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsDto> getAnalytics(HttpSession session) {
        authService.ensureRole(session, UserRole.LOCAL_ADMIN);
        User current = authService.getCurrentUser(session);
        return ResponseEntity.ok(pickupRequestService.getAnalytics(current.getLocation()));
    }
}
