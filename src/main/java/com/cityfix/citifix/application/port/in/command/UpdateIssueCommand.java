package com.cityfix.citifix.application.port.in.command;

public record UpdateIssueCommand(
        Long issueId,
        String title,
        String description,
        String status,
        String category
) {}