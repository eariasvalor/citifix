package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.UpdateIssueInputPort;
import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateIssueUseCase implements UpdateIssueInputPort {

    private final IssueRepositoryPort repositoryPort;

    @Transactional
    public UrbanIssue execute(UpdateIssueCommand command) {
        UrbanIssue issue = repositoryPort.findById(command.issueId())
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));

        IssueCategory cat = command.category() != null ? IssueCategory.valueOf(command.category()) : null;
        issue.correctDetails(command.title(), command.description(), cat);

        if (command.status() != null) {
            IssueStatus stat = IssueStatus.valueOf(command.status());
            issue.forceStatusChange(stat);
        }

        return repositoryPort.save(issue);
    }
}
