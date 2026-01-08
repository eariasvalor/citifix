package com.cityfix.citifix.application.port.in.query;

public record FindNearbyIssuesQuery(
        Double latitude,
        Double longitude,
        Double radiusInMeters,
        Integer page,
        Integer size
) {
    public FindNearbyIssuesQuery {
        if (page == null || page < 0) page = 0;
        if (size == null || size < 1) size = 10;
    }
}