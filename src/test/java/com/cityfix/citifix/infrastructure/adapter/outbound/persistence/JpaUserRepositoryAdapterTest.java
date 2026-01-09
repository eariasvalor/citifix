package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.TestcontainersConfiguration;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.UserJpaEntity;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.SpringDataUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaUserRepositoryAdapter.class, TestcontainersConfiguration.class})
class JpaUserRepositoryAdapterTest {

    @Autowired
    private JpaUserRepositoryAdapter adapter;

    @Autowired
    private SpringDataUserRepository springRepository;

    @Test
    @DisplayName("Should save a domain user into the database")
    void shouldSaveUser() {
        User domainUser = User.create("alex@cityfix.com", "secret_hash", Set.of("ROLE_USER"));

        User savedUser = adapter.save(domainUser);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("alex@cityfix.com");

        Optional<UserJpaEntity> entityInDb = springRepository.findById(savedUser.getId());
        assertThat(entityInDb).isPresent();
        assertThat(entityInDb.get().getEmail()).isEqualTo("alex@cityfix.com");
        assertThat(entityInDb.get().getRoles()).contains("ROLE_USER");
    }

    @Test
    @DisplayName("Should find a user by email and map it to domain")
    void shouldFindUserByEmail() {
        UserJpaEntity entity = new UserJpaEntity(null, "maria@cityfix.com", "hash123", Set.of("ROLE_ADMIN"));
        springRepository.save(entity);

        Optional<User> result = adapter.findByEmail("maria@cityfix.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("maria@cityfix.com");
        assertThat(result.get().getRoles()).contains("ROLE_ADMIN");
        assertThat(result.get()).isInstanceOf(User.class);
    }

    @Test
    @DisplayName("Should return empty when email does not exist")
    void shouldReturnEmptyWhenNotFound() {
        Optional<User> result = adapter.findByEmail("ghost@cityfix.com");
        assertThat(result).isEmpty();
    }
}