package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.service.ImpactRewardPolicy;
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

    public UserStats withDecrementedReported() {
        return new UserStats(this.userId, Math.max(0, this.totalReported - 1), this.inProgressCount, this.resolvedCount, this.impactPoints);
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

    public UserStats applyStatusChange(String oldStatus, String newStatus, ImpactRewardPolicy policy) {
        long pointsToAdd = policy.calculatePoints(oldStatus, newStatus);

        UserStats updated = this;

        if ("IN_PROGRESS".equals(oldStatus)) {
            updated = updated.withDecrementedInProgress();
        }

        if ("IN_PROGRESS".equals(newStatus)) {
            updated = updated.withIncrementedInProgress();
        } else if ("RESOLVED".equals(newStatus)) {
            updated = updated.withIncrementedResolved();
        }

        return updated.withAddedImpactPoints(pointsToAdd);
    }
}