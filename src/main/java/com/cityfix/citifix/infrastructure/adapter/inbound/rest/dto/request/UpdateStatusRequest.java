package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateStatusRequest(
        @NotNull(message = "New status is required")
        @Pattern(regexp = "REPORTED|IN_PROGRESS|RESOLVED", message = "Status must be REPORTED, IN_PROGRESS or RESOLVED")
        String status
) {}