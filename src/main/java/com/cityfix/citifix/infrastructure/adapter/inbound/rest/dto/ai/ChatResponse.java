package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.ai;

import java.time.LocalDateTime;

public record ChatResponse(
        String response,
        LocalDateTime timestamp
) {
    public static ChatResponse of(String response) {
        return new ChatResponse(response, LocalDateTime.now());
    }
}