package com.ewaste.service;

import com.ewaste.dto.AdminAnalyticsDto;
import com.ewaste.dto.PickupRequestCreateDto;
import com.ewaste.dto.UpdateStatusRequest;
import com.ewaste.entity.*;
import com.ewaste.exception.BadRequestException;
import com.ewaste.exception.ResourceNotFoundException;
import com.ewaste.repository.PickupRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PickupRequestService {
    private final PickupRequestRepository requestRepository;

    public PickupRequest createRequest(User user, PickupRequestCreateDto dto) {
        PickupRequest request = new PickupRequest();
        request.setUser(user);
        request.setDeviceType(dto.getDeviceType().trim());
        request.setQuantity(dto.getQuantity());
        request.setDescription(dto.getDescription() == null ? null : dto.getDescription().trim());
        request.setPickupAddress(dto.getPickupAddress().trim());
        request.setLocation(dto.getLocation().trim());
        request.setPickupDate(dto.getPickupDate());
        request.setStatus(RequestStatus.PENDING);
        return requestRepository.save(request);
    }

    public Page<PickupRequest> getUserRequests(Long userId, Pageable pageable) {
        return requestRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<PickupRequest> getRequestsForLocalAdmin(User localAdmin, RequestStatus status, String keyword, Pageable pageable) {
        if (localAdmin.getLocation() == null || localAdmin.getLocation().isBlank()) {
            throw new BadRequestException("Local admin is missing assigned location");
        }
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        return requestRepository.findAllByFilters(status, localAdmin.getLocation(), normalizedKeyword, pageable);
    }

    public Page<PickupRequest> getAllRequests(RequestStatus status, String location, String keyword, Pageable pageable) {
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        String normalizedLocation = location == null || location.isBlank() ? null : location.trim();
        return requestRepository.findAllByFilters(status, normalizedLocation, normalizedKeyword, pageable);
    }

    public PickupRequest updateStatus(User actor, Long requestId, UpdateStatusRequest payload, boolean isSuperAdmin) {
        PickupRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!isSuperAdmin && !request.getLocation().equalsIgnoreCase(actor.getLocation())) {
            throw new BadRequestException("You can only update requests in your assigned location");
        }

        request.setStatus(payload.getStatus());
        if (Boolean.TRUE.equals(payload.getAssignToMe()) && actor.getRole() == UserRole.LOCAL_ADMIN) {
            request.setAssignedAdmin(actor);
        }
        PickupRequest saved = requestRepository.save(request);

        log.info("Notification: Request #{} status updated to {} by {} ({})",
                saved.getId(), saved.getStatus(), actor.getEmail(), actor.getRole());
        return saved;
    }

    public AdminAnalyticsDto getAnalytics(String location) {
        if (location == null || location.isBlank()) {
            return new AdminAnalyticsDto(
                    requestRepository.count(),
                    requestRepository.countByStatus(RequestStatus.PENDING),
                    requestRepository.countByStatus(RequestStatus.APPROVED),
                    requestRepository.countByStatus(RequestStatus.COMPLETED),
                    requestRepository.countByStatus(RequestStatus.REJECTED)
            );
        }

        return new AdminAnalyticsDto(
                requestRepository.countByLocationIgnoreCase(location),
                requestRepository.countByLocationIgnoreCaseAndStatus(location, RequestStatus.PENDING),
                requestRepository.countByLocationIgnoreCaseAndStatus(location, RequestStatus.APPROVED),
                requestRepository.countByLocationIgnoreCaseAndStatus(location, RequestStatus.COMPLETED),
                requestRepository.countByLocationIgnoreCaseAndStatus(location, RequestStatus.REJECTED)
        );
    }
}
