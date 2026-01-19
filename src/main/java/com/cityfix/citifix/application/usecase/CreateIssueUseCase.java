package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.CreateIssueInputPort;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateIssueUseCase implements CreateIssueInputPort {

    private final IssueRepositoryPort issueRepository;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public UrbanIssue execute(CreateIssueCommand command) {
        User reporter = userRepository.findByEmail(command.reporterEmail())
                .orElseThrow(() -> new IllegalArgumentException("Reporter user not found"));

        IssueTitle title = new IssueTitle(command.title());
        Coordinates coordinates = new Coordinates(command.latitude(), command.longitude());
        UserId reporterId = new UserId(reporter.getId());

        IssueCategory category = IssueCategory.OTHER;

        if (command.category() != null && !command.category().isBlank()) {
            try {
                category = IssueCategory.valueOf(command.category().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Warning: Invalid category received '" + command.category() + "'. Defaulting to OTHER.");
            }
        }

        UrbanIssue issue = new UrbanIssue(
                null,
                title,
                command.description(),
                coordinates,
                reporterId,
                category
        );

        return issueRepository.save(issue);
    }
}