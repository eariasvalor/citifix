package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.FindAllIssuesInputPort;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FindAllIssuesUseCase implements FindAllIssuesInputPort {

    private final IssueRepositoryPort issueRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UrbanIssue> execute(String category, String status, Long reporterId, Pageable pageable) {
        return issueRepository.findAll(category, status, reporterId, pageable);
    }
}
