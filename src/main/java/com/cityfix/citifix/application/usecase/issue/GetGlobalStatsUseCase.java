package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.GetGlobalStatsInputPort;
import com.cityfix.citifix.domain.model.GlobalStats;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetGlobalStatsUseCase implements GetGlobalStatsInputPort {

    private final IssueRepositoryPort issueRepository;

    @Override
    @Transactional(readOnly = true)
    public GlobalStats execute() {
        return issueRepository.getGlobalStats();
    }
}