package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request;

import java.util.Set;

public record UpdateUserRequest(String email, Set<String> roles) {
}
