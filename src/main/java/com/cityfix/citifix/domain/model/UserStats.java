package com.cityfix.citifix.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStats {
    private final Long userId;
    private final long totalReported;
    private final long inProgressCount;
    private final long resolvedCount;
    private final long impactPoints;


    public String getRank() {
        if (impactPoints >= 500) return "CITY HERO";
        if (impactPoints >= 200) return "ACTIVE CITIZEN";
        if (impactPoints >= 50)  return "COMMITTED NEIGHBOR";
        return "NEWCOMER";
    }

    public UserStats withIncrementedReported() {
        return new UserStats(
                this.userId,
                this.totalReported + 1,
                this.inProgressCount,
                this.resolvedCount,
                this.impactPoints
        );
    }

    public UserStats withDecrementedInProgress() {
        return new UserStats(userId, totalReported, Math.max(0, inProgressCount - 1), resolvedCount, impactPoints);
    }

    public UserStats withIncrementedInProgress() {
        return new UserStats(userId, totalReported, inProgressCount + 1, resolvedCount, impactPoints);
    }

    public UserStats withIncrementedResolved() {
        return new UserStats(userId, totalReported, inProgressCount, resolvedCount + 1, impactPoints);
    }

    public UserStats withAddedImpactPoints(long points) {
        return new UserStats(userId, totalReported, inProgressCount, resolvedCount, impactPoints + points);
    }
}