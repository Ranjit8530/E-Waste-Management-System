package com.ewaste.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminAnalyticsDto {
    private long totalRequests;
    private long pendingRequests;
    private long approvedRequests;
    private long completedRequests;
    private long rejectedRequests;
}
