package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.CreateIssueInputPort;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.ImageStoragePort;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateIssueUseCase implements CreateIssueInputPort {

    private final IssueRepositoryPort issueRepository;
    private final UserRepositoryPort userRepository;
    private final ImageStoragePort imageStorage;

    @Override
    @Transactional
    public UrbanIssue execute(CreateIssueCommand command, MultipartFile image) {
        User reporter = userRepository.findByEmail(command.reporterEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = imageStorage.upload(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        IssueTitle title = new IssueTitle(command.title());
        Coordinates coordinates = new Coordinates(command.latitude(), command.longitude());
        UserId reporterId = reporter.getId();

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
                new IssueTitle(command.title()),
                command.description(),
                new Coordinates(command.latitude(), command.longitude()),
                reporter.getId(),
                IssueStatus.REPORTED,
                IssueCategory.valueOf(command.category()),
                imageUrl,
                LocalDateTime.now()
        );

        return issueRepository.save(issue);
    }
}