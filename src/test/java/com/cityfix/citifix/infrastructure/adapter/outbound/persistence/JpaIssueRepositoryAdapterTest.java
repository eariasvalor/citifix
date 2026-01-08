package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.SpringDataIssueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaIssueRepositoryAdapter.class)
class JpaIssueRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private JpaIssueRepositoryAdapter adapter;

    @Autowired
    private SpringDataIssueRepository springRepository;

    @Test
    @DisplayName("Should save a domain issue into the database correctly")
    void shouldSaveIssue() {
        var issue = new UrbanIssue(
                null,
                new IssueTitle("Bump in the street"),
                new Coordinates(41.38, 2.17),
                new UserId(99L)
        );

        UrbanIssue savedIssue = adapter.save(issue);

        assertThat(savedIssue.getId()).isNotNull();

        var entityInDb = springRepository.findById(savedIssue.getId()).orElseThrow();
        assertThat(entityInDb.getTitle()).isEqualTo("Bump in the street");
        assertThat(entityInDb.getLatitude()).isEqualTo(41.38);
    }

    @Test
    @DisplayName("Should find issues within radius and exclude far ones")
    void shouldFindNearbyIssues() {
        var centerIssue = new UrbanIssue(null, new IssueTitle("Center"), new Coordinates(41.3870, 2.1700), new UserId(1L));
        adapter.save(centerIssue);

        var farIssue = new UrbanIssue(null, new IssueTitle("Far away"), new Coordinates(40.4168, -3.7038), new UserId(1L));
        adapter.save(farIssue);

        var results = adapter.findNearby(41.3870, 2.1700, 1000.0, 0, 1);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle().value()).isEqualTo("Center");
    }
}