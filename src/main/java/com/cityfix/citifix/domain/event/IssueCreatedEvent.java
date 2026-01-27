package com.cityfix.citifix.domain.event;

import com.cityfix.citifix.domain.model.UrbanIssue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IssueCreatedEvent {
    private final UrbanIssue issue;
}