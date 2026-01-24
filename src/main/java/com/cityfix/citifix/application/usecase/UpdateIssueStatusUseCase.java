package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.UpdateIssueStatusInputPort;
import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateIssueStatusUseCase implements UpdateIssueStatusInputPort {

    private final IssueRepositoryPort repositoryPort;

    @Transactional
    public UrbanIssue execute(UpdateIssueStatusCommand command) {
        UrbanIssue issue = repositoryPort.findById(command.issueId())
                .orElseThrow(() -> new IllegalArgumentException("Issue not found with ID: " + command.issueId()));

        UrbanIssue updatedIssue = switch (command.newStatus()) {
            case "IN_PROGRESS" -> issue.markAsInProgress();
            case "RESOLVED" -> issue.resolve();
            default -> throw new IllegalArgumentException("Invalid status");
        };

        return repositoryPort.save(updatedIssue);
    }
}