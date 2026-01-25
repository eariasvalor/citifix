package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.UpdateIssueInputPort;
import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.port.out.ImageStoragePort;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UpdateIssueUseCase implements UpdateIssueInputPort {

    private final IssueRepositoryPort repositoryPort;
    private final ImageStoragePort imageStoragePort;

    @Override
    @Transactional
    public UrbanIssue execute(UpdateIssueCommand command) throws IOException {
        UrbanIssue issue = repositoryPort.findById(command.issueId())
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));

        IssueCategory cat = command.category() != null ? IssueCategory.valueOf(command.category()) : null;
        UrbanIssue updatedIssue = issue.updateDetails(command.title(), command.description(), cat);

        if (command.image() != null && !command.image().isEmpty()) {
            String newImageUrl = imageStoragePort.upload(command.image());
            updatedIssue = updatedIssue.withImageUrl(newImageUrl);
        }

        if (command.status() != null) {
            IssueStatus newStatus = IssueStatus.valueOf(command.status());
            updatedIssue = applyStatusChange(updatedIssue, newStatus);
        }

        return repositoryPort.save(updatedIssue);
    }

    private UrbanIssue applyStatusChange(UrbanIssue issue, IssueStatus newStatus) {
        return switch (newStatus) {
            case IN_PROGRESS -> issue.markAsInProgress();
            case RESOLVED -> issue.resolve();
            default -> issue;
        };
    }
}