package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.UpdateIssueInputPort;
import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.domain.event.IssueStatusChangedEvent;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.port.out.DomainEventPublisherPort;
import com.cityfix.citifix.domain.port.out.ImageStoragePort;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UpdateIssueUseCase implements UpdateIssueInputPort {

    private final IssueRepositoryPort repositoryPort;
    private final ImageStoragePort imageStoragePort;
    private final DomainEventPublisherPort eventPublisher;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public UrbanIssue execute(UpdateIssueCommand command, String requesterEmail) throws IOException {
        UrbanIssue originalIssue = repositoryPort.findById(command.issueId())
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = requester.getRoles().contains("ROLE_ADMIN");
        boolean isOwner = originalIssue.getReporterId().value().equals(requester.getId().value());

        if (!isAdmin && !isOwner) {
            throw new SecurityException("Access denied: You can only edit your own issues");
        }

        IssueStatus statusBefore = originalIssue.getStatus();

        IssueCategory cat = command.category() != null ? IssueCategory.valueOf(command.category()) : null;
        UrbanIssue updatedIssue = originalIssue.updateDetails(command.title(), command.description(), cat);

        if (command.image() != null && !command.image().isEmpty()) {
            String newImageUrl = imageStoragePort.upload(command.image());
            updatedIssue = updatedIssue.withImageUrl(newImageUrl);
        }

        if (command.status() != null && isAdmin) {
            IssueStatus nextStatus = IssueStatus.valueOf(command.status());
            updatedIssue = applyStatusChange(updatedIssue, nextStatus);
        } else if (command.status() != null && !isAdmin) {
            System.out.println("User " + requesterEmail + " tried to change status. Action ignored.");
        }

        UrbanIssue savedIssue = repositoryPort.save(updatedIssue);

        if (statusBefore != savedIssue.getStatus()) {
            eventPublisher.publishIssueStatusChanged(new IssueStatusChangedEvent(
                    savedIssue.getReporterId().value(),
                    statusBefore.name(),
                    savedIssue.getStatus().name(),
                    false
            ));
        }

        return savedIssue;
    }

    private UrbanIssue applyStatusChange(UrbanIssue issue, IssueStatus newStatus) {
        return switch (newStatus) {
            case IN_PROGRESS -> issue.markAsInProgress();
            case RESOLVED -> issue.resolve();
            case REPORTED -> issue.backToReported();
            default -> issue;
        };
    }
}