package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response;

public record IssueResponse(
        Long id,
        String title,
        String description,
        String status,
        String category,
        Double latitude,
        Double longitude
) {}