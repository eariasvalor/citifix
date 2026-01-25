package com.cityfix.citifix.application.port.in.command;

import java.util.Set;

public record UpdateUserCommand(
        Long userId,
        String email,
        Set<String> roles) {
}
