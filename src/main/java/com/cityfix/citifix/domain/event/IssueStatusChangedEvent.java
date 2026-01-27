package com.cityfix.citifix.domain.event;

import lombok.Value;

@Value
public class IssueStatusChangedEvent {
    Long userId;
    String oldStatus;
    String newStatus;
    boolean isNewIssue;
}