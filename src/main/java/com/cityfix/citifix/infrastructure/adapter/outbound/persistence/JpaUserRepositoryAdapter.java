package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springRepository;

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);
        UserJpaEntity savedEntity = springRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public boolean existsByEmail(String email) {
        return springRepository.existsByEmail(email);
    }

    @Override
    public long count() {
        return springRepository.count();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springRepository.findByEmail(email)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return springRepository.findById(id.value())
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public Page<User> findAll(int page, int size) {
        return springRepository.findAll(PageRequest.of(page, size))
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public void deleteById(UserId id) {
        springRepository.deleteById(id.value());
    }
    @Override
    public long countByRole(String role) {
        return springRepository.countByRolesContaining(role);
    }
}