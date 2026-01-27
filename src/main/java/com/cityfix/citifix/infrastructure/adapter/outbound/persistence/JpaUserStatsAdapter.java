package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.domain.model.UserStats;
import com.cityfix.citifix.domain.port.out.UserStatsRepositoryPort;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.UserStatsEntity;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaUserStatsAdapter implements UserStatsRepositoryPort {
    private final UserStatsRepository repository;

    @Override
    public Optional<UserStats> findByUserId(Long userId) {
        return repository.findById(userId)
                .map(entity -> new UserStats(
                        entity.getUserId(),
                        entity.getTotalReported(),
                        entity.getInProgressCount(),
                        entity.getResolvedCount(),
                        entity.getImpactPoints()
                ));
    }

    @Override
    public UserStats save(UserStats userStats) {
        UserStatsEntity entity = repository.findById(userStats.getUserId())
                .orElse(new UserStatsEntity());

        entity.setUserId(userStats.getUserId());
        entity.setTotalReported(userStats.getTotalReported());
        entity.setInProgressCount(userStats.getInProgressCount());
        entity.setResolvedCount(userStats.getResolvedCount());
        entity.setImpactPoints(userStats.getImpactPoints());

        return repository.saveAndFlush(entity).toDomain();
    }

}