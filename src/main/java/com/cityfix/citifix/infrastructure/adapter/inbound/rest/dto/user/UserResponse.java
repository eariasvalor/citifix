package com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.user;

import com.cityfix.citifix.domain.model.User;
import java.util.Set;

public record UserResponse(
        Long id,
        String email,
        Set<String> roles
) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId() != null ? user.getId().value() : null,
                user.getEmail(),
                user.getRoles()
        );
    }
}