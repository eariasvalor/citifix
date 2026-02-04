package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.DeleteIssueInputPort;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteIssueUseCase implements DeleteIssueInputPort {

    private final IssueRepositoryPort issueRepository;
    private final UserRepositoryPort userRepository;

    @Override
    @Transactional
    public void execute(Long issueId, String requesterEmail) {
        UrbanIssue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found"));

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = requester.getRoles().contains("ROLE_ADMIN");
        boolean isOwner = issue.getReporterId().value().equals(requester.getId().value());

        if (!isAdmin && !isOwner) {
            throw new SecurityException("Access denied: You can only delete your own issues");
        }

        issueRepository.deleteById(issueId);
    }
}