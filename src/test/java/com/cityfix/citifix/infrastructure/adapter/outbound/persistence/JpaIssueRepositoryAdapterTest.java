package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.mapper.IssueMapper;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.SpringDataIssueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@Import({JpaIssueRepositoryAdapter.class, IssueMapper.class})
class JpaIssueRepositoryAdapterTest {

    @Autowired
    private JpaIssueRepositoryAdapter adapter;

    @Autowired
    private SpringDataIssueRepository jpaRepository;

    @Test
    @DisplayName("Should save issue and convert correctly")
    void shouldSaveIssue() {
        UrbanIssue issue = new UrbanIssue(
                null,
                new IssueTitle("Broken Bench"),
                new Coordinates(40.0, 2.0),
                new UserId(5L),
                IssueCategory.OTHER
        );

        UrbanIssue savedIssue = adapter.save(issue);

        assertThat(savedIssue.getId()).isNotNull();
        assertThat(savedIssue.getTitle().value()).isEqualTo("Broken Bench");

        Optional<IssueEntity> inDb = jpaRepository.findById(savedIssue.getId());
        assertThat(inDb).isPresent();
        assertThat(inDb.get().getReporterId()).isEqualTo(5L);
        assertThat(inDb.get().getStatus()).isEqualTo(IssueStatus.REPORTED);
        assertThat(inDb.get().getCategory()).isEqualTo(IssueCategory.OTHER);
    }

    @Test
    @DisplayName("Should find nearby issues")
    void shouldFindNearbyIssues() {
        IssueEntity entity1 = IssueEntity.builder()
                .title("Issue 1")
                .latitude(40.0).longitude(2.0)
                .reporterId(1L)
                .status(IssueStatus.REPORTED)
                .build();

        jpaRepository.save(entity1);

        List<UrbanIssue> result = adapter.findNearby(40.0, 2.0, 10.0, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle().value()).isEqualTo("Issue 1");
    }
}