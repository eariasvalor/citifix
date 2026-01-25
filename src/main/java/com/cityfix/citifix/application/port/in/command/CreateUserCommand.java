package com.cityfix.citifix.application.port.in.command;

import java.util.Set;

public record CreateUserCommand(
        String email,
        String password,
        Set<String> roles
) {}