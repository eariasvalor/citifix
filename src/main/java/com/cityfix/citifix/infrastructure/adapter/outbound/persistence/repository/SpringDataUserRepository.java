package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository;

import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}