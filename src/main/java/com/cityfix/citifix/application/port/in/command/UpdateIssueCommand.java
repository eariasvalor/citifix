package com.cityfix.citifix.application.port.in.command;

import org.springframework.web.multipart.MultipartFile;

public record UpdateIssueCommand(
        Long issueId,
        String title,
        String description,
        String status,
        String category,
        MultipartFile image
) {}