package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.ai;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "The message cannot be empty")
        String message
) {}