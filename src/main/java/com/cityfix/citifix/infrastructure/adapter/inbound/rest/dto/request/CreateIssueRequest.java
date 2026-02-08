package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request;

import jakarta.validation.constraints.*;

public record CreateIssueRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Latitude is required")
        @Min(-90) @Max(90)
        Double latitude,

        @NotNull(message = "Longitude is required")
        @Min(-180) @Max(180)
        Double longitude,

        @Pattern(regexp = "LIGHTING|ROAD|TRASH|OTHER", message = "Invalid category")
        String category
) {
}