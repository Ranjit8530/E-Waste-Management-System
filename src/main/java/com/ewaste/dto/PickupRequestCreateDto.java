package com.ewaste.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PickupRequestCreateDto {

    @NotBlank(message = "Device type is required")
    @Size(max = 50)
    private String deviceType;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity is too large")
    private Integer quantity;

    @Size(max = 1000)
    private String description;

    @NotBlank(message = "Pickup address is required")
    @Size(max = 255)
    private String pickupAddress;

    @NotBlank(message = "Location is required")
    @Size(max = 80)
    private String location;

    @NotNull
    @FutureOrPresent(message = "Pickup date cannot be in the past")
    private LocalDate pickupDate;
}
