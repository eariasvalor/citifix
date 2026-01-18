package com.cityfix.citifix.application.port.in.command;

public record CreateIssueCommand(
        String title,
        String description,
        Double latitude,
        Double longitude,
        String category,
        String reporterEmail
) {}