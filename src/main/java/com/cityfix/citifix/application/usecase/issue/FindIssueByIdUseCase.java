package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.FindIssueByIdInputPort;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindIssueByIdUseCase implements FindIssueByIdInputPort {

    private final IssueRepositoryPort issueRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<UrbanIssue> execute(Long id) {
        return issueRepository.findById(id);
    }
}