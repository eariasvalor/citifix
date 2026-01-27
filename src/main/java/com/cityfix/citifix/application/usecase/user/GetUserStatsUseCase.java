package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.domain.model.UserStats;
import com.cityfix.citifix.domain.port.out.UserStatsRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserStatsUseCase {
    private final UserStatsRepositoryPort statsRepositoryPort;

    public UserStats execute(Long userId) {
        return statsRepositoryPort.findByUserId(userId)
                .orElse(new UserStats(userId, 0, 0, 0, 0));
    }
}