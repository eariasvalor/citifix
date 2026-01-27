package com.cityfix.citifix.infrastructure.adapter.outbound.event;

import com.cityfix.citifix.domain.event.IssueStatusChangedEvent;
import com.cityfix.citifix.domain.port.out.DomainEventPublisherPort;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisherPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishIssueStatusChanged(IssueStatusChangedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {
        applicationEventPublisher.publishEvent(event);
    }
}