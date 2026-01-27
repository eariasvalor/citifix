package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository;

import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.UserStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStatsEntity, Long> {

}