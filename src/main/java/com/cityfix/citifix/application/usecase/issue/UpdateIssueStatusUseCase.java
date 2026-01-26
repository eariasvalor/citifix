package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.UpdateIssueStatusInputPort;
import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
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

        IssueStatus nextStatus;
        try {
            nextStatus = IssueStatus.valueOf(command.newStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + command.newStatus());
        }

        UrbanIssue updatedIssue = switch (command.newStatus().toUpperCase()) {
            case "REPORTED" -> issue.backToReported();
            case "IN_PROGRESS" -> issue.markAsInProgress();
            case "RESOLVED" -> issue.resolve();
            default -> throw new IllegalArgumentException("Invalid status: " + command.newStatus());
        };

        return repositoryPort.save(updatedIssue);
    }
}