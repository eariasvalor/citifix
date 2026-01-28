package com.cityfix.citifix.application.listener;

import com.cityfix.citifix.domain.event.IssueCreatedEvent;
import com.cityfix.citifix.domain.event.IssueStatusChangedEvent;
import com.cityfix.citifix.domain.model.UserStats;
import com.cityfix.citifix.domain.port.out.UserStatsRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserStatsEventListener {

    private final UserStatsRepositoryPort statsRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onIssueStatusChanged(IssueStatusChangedEvent event) {
        if (event.isNewIssue() || event.getOldStatus().equals(event.getNewStatus())) {
            return;
        }

        UserStats stats = statsRepository.findByUserId(event.getUserId())
                .orElse(new UserStats(event.getUserId(), 0, 0, 0, 0));

        UserStats updatedStats = applyStatusTransitions(stats, event.getOldStatus(), event.getNewStatus());

        statsRepository.save(updatedStats);
    }

    private UserStats applyStatusTransitions(UserStats stats, String oldStatus, String newStatus) {
        UserStats result = stats;

        if ("IN_PROGRESS".equals(oldStatus)) {
            result = result.withDecrementedInProgress();
        }

        result = switch (oldStatus) {
            case "REPORTED" -> result.withDecrementedReported();
            case "IN_PROGRESS" -> result.withDecrementedInProgress();
            default -> result;
        };

        result = switch (newStatus) {
            case "IN_PROGRESS" -> result.withIncrementedInProgress();
            case "RESOLVED" -> result.withIncrementedResolved().withAddedImpactPoints(100);
            case "REPORTED" -> result.withIncrementedReported();
            default -> result;
        };

        return result;
    }

    @EventListener
    @Transactional
    public void handleIssueCreated(IssueCreatedEvent event) {
        Long userId = event.getIssue().getReporterId().value();
        UserStats stats = statsRepository.findByUserId(userId)
                .orElse(new UserStats(userId, 0, 0, 0, 0));

        statsRepository.save(stats.withIncrementedReported());
    }
}