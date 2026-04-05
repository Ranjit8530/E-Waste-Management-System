package com.ewaste.controller;

import com.ewaste.dto.AdminActionResponse;
import com.ewaste.dto.AdminAnalyticsDto;
import com.ewaste.dto.PickupRequestResponseDto;
import com.ewaste.dto.UpdateStatusRequest;
import com.ewaste.entity.AccountStatus;
import com.ewaste.entity.RequestStatus;
import com.ewaste.entity.User;
import com.ewaste.entity.UserRole;
import com.ewaste.service.AuthService;
import com.ewaste.service.PickupRequestService;
import com.ewaste.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/superadmin")
@RequiredArgsConstructor
public class SuperAdminController {
    private final AuthService authService;
    private final UserService userService;
    private final PickupRequestService pickupRequestService;

    @GetMapping("/admins")
    public ResponseEntity<Page<Map<String, Object>>> getAdmins(HttpSession session,
                                                               @RequestParam(required = false) AccountStatus status,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        authService.ensureRole(session, UserRole.SUPER_ADMIN);
        Page<Map<String, Object>> admins = userService.listByRole(UserRole.LOCAL_ADMIN, status, PageRequest.of(page, size))
                .map(this::toUserResponse);
        return ResponseEntity.ok(admins);
    }

    @PutMapping("/admin/{id}/approve")
    public ResponseEntity<AdminActionResponse> approveAdmin(@PathVariable Long id,
                                                            @RequestBody Map<String, String> body,
                                                            HttpSession session) {
        authService.ensureRole(session, UserRole.SUPER_ADMIN);
        return ResponseEntity.ok(userService.approveLocalAdmin(id, body.get("location")));
    }

    @PutMapping("/admin/{id}/reject")
    public ResponseEntity<AdminActionResponse> rejectAdmin(@PathVariable Long id, HttpSession session) {
        authService.ensureRole(session, UserRole.SUPER_ADMIN);
        return ResponseEntity.ok(userService.rejectLocalAdmin(id));
    }

    @GetMapping("/all-requests")
    public ResponseEntity<Page<PickupRequestResponseDto>> getAllRequests(HttpSession session,
                                                                         @RequestParam(required = false) RequestStatus status,
                                                                         @RequestParam(required = false) String location,
                                                                         @RequestParam(required = false) String keyword,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        authService.ensureRole(session, UserRole.SUPER_ADMIN);
        Page<PickupRequestResponseDto> response = pickupRequestService
                .getAllRequests(status, location, keyword, PageRequest.of(page, size))
                .map(PickupRequestResponseDto::fromEntity);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/request/{id}/status")
    public ResponseEntity<PickupRequestResponseDto> overrideStatus(@PathVariable Long id,
                                                                   @Valid @RequestBody UpdateStatusRequest request,
                                                                   HttpSession session) {
        authService.ensureRole(session, UserRole.SUPER_ADMIN);
        User current = authService.getCurrentUser(session);
        return ResponseEntity.ok(PickupRequestResponseDto.fromEntity(
                pickupRequestService.updateStatus(current, id, request, true)));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<Map<String, Object>>> getAllUsers(HttpSession session,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        authService.ensureRole(session, UserRole.SUPER_ADMIN);
        Page<Map<String, Object>> users = userService.listAll(PageRequest.of(page, size)).map(this::toUserResponse);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/analytics")
    public ResponseEntity<AdminAnalyticsDto> getGlobalAnalytics(HttpSession session) {
        authService.ensureRole(session, UserRole.SUPER_ADMIN);
        return ResponseEntity.ok(pickupRequestService.getAnalytics(null));
    }

    private Map<String, Object> toUserResponse(User user) {
        Map<String, Object> out = new java.util.HashMap<>();
        out.put("id", user.getId());
        out.put("name", user.getName());
        out.put("email", user.getEmail());
        out.put("phone", user.getPhone());
        out.put("address", user.getAddress());
        out.put("role", user.getRole().name());
        out.put("status", user.getStatus().name());
        out.put("location", user.getLocation());
        return out;
    }
}
