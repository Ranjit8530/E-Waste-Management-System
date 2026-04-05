package com.ewaste.dto;

import com.ewaste.entity.PickupRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PickupRequestResponseDto {
    private Long id;
    private Long userId;
    private Long assignedAdminId;
    private String assignedAdminName;
    private String userName;
    private String userEmail;
    private String deviceType;
    private Integer quantity;
    private String description;
    private String pickupAddress;
    private String location;
    private LocalDate pickupDate;
    private String status;
    private LocalDateTime createdAt;

    public static PickupRequestResponseDto fromEntity(PickupRequest request) {
        return new PickupRequestResponseDto(
                request.getId(),
                request.getUser().getId(),
                request.getAssignedAdmin() == null ? null : request.getAssignedAdmin().getId(),
                request.getAssignedAdmin() == null ? null : request.getAssignedAdmin().getName(),
                request.getUser().getName(),
                request.getUser().getEmail(),
                request.getDeviceType(),
                request.getQuantity(),
                request.getDescription(),
                request.getPickupAddress(),
                request.getLocation(),
                request.getPickupDate(),
                request.getStatus().name(),
                request.getCreatedAt()
        );
    }
}
