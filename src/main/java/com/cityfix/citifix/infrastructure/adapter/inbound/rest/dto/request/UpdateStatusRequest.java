package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateStatusRequest(
        @NotNull(message = "New status is required")
        @Pattern(regexp = "IN_PROGRESS|RESOLVED", message = "Status must be IN_PROGRESS or RESOLVED")
        String status
) {}