package com.ewaste.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminActionResponse {
    private Long id;
    private String status;
    private String location;
    private String message;
}
