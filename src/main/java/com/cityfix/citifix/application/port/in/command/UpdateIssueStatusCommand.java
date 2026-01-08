package com.cityfix.citifix.application.port.in.command;

public record UpdateIssueStatusCommand(
        Long issueId,
        String newStatus
) {}