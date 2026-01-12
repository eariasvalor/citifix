package com.cityfix.citifix.application.port.in.command;

public record CreateIssueCommand(
        String title,
        Double latitude,
        Double longitude,
        String reporterEmail
) {}