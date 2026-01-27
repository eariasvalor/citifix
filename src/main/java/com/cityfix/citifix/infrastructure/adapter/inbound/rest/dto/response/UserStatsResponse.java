package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response;

import com.cityfix.citifix.domain.model.UserStats;

public record UserStatsResponse(
        Long userId,
        long totalReported,
        long inProgressCount,
        long resolvedCount,
        long impactPoints,
        String rank
) {
    public static UserStatsResponse fromDomain(UserStats stats) {
        return new UserStatsResponse(
                stats.getUserId(),
                stats.getTotalReported(),
                stats.getInProgressCount(),
                stats.getResolvedCount(),
                stats.getImpactPoints(),
                stats.getRank()
        );
    }
}