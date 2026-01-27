package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity;

import com.cityfix.citifix.domain.model.UserStats;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsEntity {

    @Id
    private Long userId;

    private long totalReported = 0;
    private long inProgressCount = 0;
    private long resolvedCount = 0;
    private long impactPoints = 0;


    public static UserStatsEntity fromDomain(UserStats domain) {
        UserStatsEntity entity = new UserStatsEntity();
        entity.setUserId(domain.getUserId());
        entity.setTotalReported(domain.getTotalReported());
        entity.setInProgressCount(domain.getInProgressCount());
        entity.setResolvedCount(domain.getResolvedCount());
        entity.setImpactPoints(domain.getImpactPoints());
        return entity;
    }

    public UserStats toDomain() {
        return new UserStats(
                this.userId,
                this.totalReported,
                this.inProgressCount,
                this.resolvedCount,
                this.impactPoints
        );
    }
}