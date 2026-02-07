package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.domain.model.GlobalStats;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.SpringDataIssueRepository;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.mapper.IssueMapper; // Importante
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaIssueRepositoryAdapter.class, IssueMapper.class})
class GlobalStatsPersistenceTest {

    @Autowired
    private JpaIssueRepositoryAdapter adapter;

    @Autowired
    private SpringDataIssueRepository repository;

    @Test
    void shouldReturnCorrectAggregatedStats() {
        repository.deleteAll();
        repository.save(createIssue(IssueStatus.RESOLVED, IssueCategory.ROAD));
        repository.save(createIssue(IssueStatus.REPORTED, IssueCategory.ROAD));

        GlobalStats stats = adapter.getGlobalStats();

        assertThat(stats.getTotalIssues()).isEqualTo(2);
        assertThat(stats.getIssuesByStatus().get("RESOLVED")).isEqualTo(1);
        assertThat(stats.getIssuesByStatus().get("REPORTED")).isEqualTo(1);
        assertThat(stats.getIssuesByCategory().get("ROAD")).isEqualTo(2);
    }

    private IssueEntity createIssue(IssueStatus status, IssueCategory category) {
        return IssueEntity.builder()
                .title("Test Issue")
                .reporterId(1L)
                .status(status)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();
    }
}