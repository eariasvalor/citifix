package com.cityfix.citifix.domain.port.out;

import com.cityfix.citifix.domain.model.UserStats;
import java.util.Optional;

public interface UserStatsRepositoryPort {
    Optional<UserStats> findByUserId(Long userId);
    UserStats save(UserStats userStats);
}