package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.domain.model.GlobalStats;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGlobalStatsUseCaseTest {

    @Mock
    private IssueRepositoryPort issueRepository;

    @InjectMocks
    private GetGlobalStatsUseCase useCase;

    @Test
    void shouldInvokeRepository() {
        when(issueRepository.getGlobalStats())
                .thenReturn(new GlobalStats(0, Map.of(), Map.of()));

        useCase.execute();

        verify(issueRepository).getGlobalStats();
    }
}