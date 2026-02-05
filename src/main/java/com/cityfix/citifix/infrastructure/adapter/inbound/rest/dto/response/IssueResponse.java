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
        String imageUrl,
        java.time.LocalDateTime createdAt
) {

    public static IssueResponse fromDomain(UrbanIssue issue) {
        return new IssueResponse(
                issue.getId(),
                issue.getTitle() != null ? issue.getTitle().value() : null,
                issue.getDescription(),
                issue.getCoordinates() != null ? issue.getCoordinates().latitude() : null,
                issue.getCoordinates() != null ? issue.getCoordinates().longitude() : null,
                issue.getStatus() != null ? issue.getStatus().name() : null,
                issue.getCategory() != null ? issue.getCategory().name() : "OTHER",
                issue.getReporterId() != null ? issue.getReporterId().getValue().toString() : null,
                issue.getImageUrl(),
                issue.getCreatedAt()
        );
    }

    public record MessageResponse(String message) {}
}