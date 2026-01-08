package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response;

import java.util.List;

public record ErrorResponse(
        String type,
        String message,
        List<String> details
) {}