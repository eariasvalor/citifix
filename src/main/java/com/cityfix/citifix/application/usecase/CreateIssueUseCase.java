package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.CreateIssueInputPort;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateIssueUseCase implements CreateIssueInputPort {

    private final IssueRepositoryPort repositoryPort;

    public CreateIssueUseCase(IssueRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public UrbanIssue execute(CreateIssueCommand command) {
        var title = new IssueTitle(command.title());
        var coordinates = new Coordinates(command.latitude(), command.longitude());
        var reporterId = new UserId(command.reporterId());

        var issue = new UrbanIssue(null, title, coordinates, reporterId);

        return repositoryPort.save(issue);
    }
}