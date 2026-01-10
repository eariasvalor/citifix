package com.cityfix.citifix.application.port.in.command;

public record CreateUserCommand(
        String email,
        String password,
        String role
) {}