package com.cityfix.citifix.domain.service;

public class DefaultImpactRewardPolicy implements ImpactRewardPolicy {
    private static final long RESOLUTION_POINTS = 100;

    @Override
    public long calculatePoints(String oldStatus, String newStatus) {
        if ("RESOLVED".equals(newStatus) && !"RESOLVED".equals(oldStatus)) {
            return RESOLUTION_POINTS;
        }
        return 0;
    }

}
