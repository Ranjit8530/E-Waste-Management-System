package com.ewaste.dto;

import com.ewaste.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {
    @NotNull(message = "Status is required")
    private RequestStatus status;

    private Boolean assignToMe = false;
}
