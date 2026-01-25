package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.DeleteIssueInputPort;
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
        if (!issueRepository.existsById(issueId)) {
            throw new IllegalArgumentException("Issue not found");
        }

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!requester.getRoles().contains("ROLE_ADMIN")) {
            throw new SecurityException("Access denied: Only admins can delete issues");
        }

        issueRepository.deleteById(issueId);
    }
}