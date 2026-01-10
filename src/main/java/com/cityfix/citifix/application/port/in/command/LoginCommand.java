package com.cityfix.citifix.application.port.in.command;

public record LoginCommand(
        String email,
        String password
) {}