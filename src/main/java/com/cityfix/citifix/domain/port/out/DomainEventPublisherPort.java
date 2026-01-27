package com.cityfix.citifix.domain.port.out;

import com.cityfix.citifix.domain.event.IssueStatusChangedEvent;

public interface DomainEventPublisherPort {
    void publishIssueStatusChanged(IssueStatusChangedEvent event);
    void publishEvent(Object event);
}