package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.domain.model.UserStats;
import com.cityfix.citifix.domain.port.out.UserStatsRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetUserStatsUseCaseTest {

    @Mock
    private UserStatsRepositoryPort statsRepositoryPort;

    @InjectMocks
    private GetUserStatsUseCase getUserStatsUseCase;

    @Test
    @DisplayName("Should return existing stats when user has activity")
    void shouldReturnExistingStats() {
        Long userId = 1L;
        UserStats expectedStats = new UserStats(userId, 5, 2, 3, 250);
        given(statsRepositoryPort.findByUserId(userId)).willReturn(Optional.of(expectedStats));

        UserStats actualStats = getUserStatsUseCase.execute(userId);

        assertNotNull(actualStats);
        assertEquals(5, actualStats.getTotalReported());
        assertEquals(250, actualStats.getImpactPoints());
        assertEquals("ACTIVE CITIZEN", actualStats.getRank());
        verify(statsRepositoryPort).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return default empty stats when user is not found in stats table")
    void shouldReturnDefaultStatsWhenNotFound() {
        Long userId = 99L;
        given(statsRepositoryPort.findByUserId(userId)).willReturn(Optional.empty());

        UserStats actualStats = getUserStatsUseCase.execute(userId);

        assertNotNull(actualStats);
        assertEquals(userId, actualStats.getUserId());
        assertEquals(0, actualStats.getTotalReported());
        assertEquals(0, actualStats.getImpactPoints());
        assertEquals("NEWCOMER", actualStats.getRank());
    }
}