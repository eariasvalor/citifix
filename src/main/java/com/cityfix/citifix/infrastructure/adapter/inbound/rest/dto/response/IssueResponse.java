package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response;

import com.cityfix.citifix.domain.model.UrbanIssue;

public record IssueResponse(
        Long id,
        String title,
        String description,
        Double latitude,
        Double longitude,
        String status,
        String category,
        String reporterId,
        String imageUrl
) {

    public static IssueResponse fromDomain(UrbanIssue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getTitle().value(),
                issue.getDescription(),
                issue.getCoordinates().latitude(),
                issue.getCoordinates().longitude(),
                issue.getStatus().name(),
                issue.getCategory().name(),
                issue.getReporterId().getValue().toString(),
                issue.getImageUrl()
        );
    }
}