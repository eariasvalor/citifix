package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.issue;

import jakarta.validation.constraints.Pattern;

public record UpdateIssueRequest(

        String title,

        String description,

        @Pattern(regexp = "REPORTED|IN_PROGRESS|RESOLVED|OPEN", message = "Invalid status value")
        String status,

        @Pattern(regexp = "LIGHTING|ROAD|TRASH|OTHER", message = "Invalid category value")
        String category
) {
}
