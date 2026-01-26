package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.FindIssuesByUserIdInputPort;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindIssuesByUserIdUseCase implements FindIssuesByUserIdInputPort {
    private final IssueRepositoryPort issueRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UrbanIssue> execute(Long userId, String status, String category, Pageable pageable) {
        return issueRepository.findByReporterIdWithFilters(userId, status, category, pageable);
    }
}

