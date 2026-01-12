package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.issue;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateIssueRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Latitude is required")
        @Min(-90) @Max(90)
        Double latitude,

        @NotNull(message = "Longitude is required")
        @Min(-180) @Max(180)
        Double longitude
) {}