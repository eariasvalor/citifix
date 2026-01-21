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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

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
        "spring.datasource.password=",
        "spring.sql.init.mode=never"
})
@Import({JpaIssueRepositoryAdapter.class, IssueMapper.class})
class JpaIssueRepositoryAdapterTest {

    @Autowired
    private JpaIssueRepositoryAdapter adapter;

    @Autowired
    private SpringDataIssueRepository jpaRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should save issue with image URL and convert correctly")
    void shouldSaveIssue() {
        String expectedImageUrl = "http://cloudinary.com/bench.jpg";

        UrbanIssue issue = new UrbanIssue(
                null,
                new IssueTitle("Broken Bench"),
                "Description",
                new Coordinates(40.0, 2.0),
                new UserId(5L),
                IssueStatus.REPORTED,
                IssueCategory.OTHER,
                expectedImageUrl
        );

        UrbanIssue savedIssue = adapter.save(issue);

        assertThat(savedIssue.getId()).isNotNull();
        assertThat(savedIssue.getImageUrl()).isEqualTo(expectedImageUrl);

        Optional<IssueEntity> inDb = jpaRepository.findById(savedIssue.getId());
        assertThat(inDb).isPresent();
        assertThat(inDb.get().getReporterId()).isEqualTo(5L);
        assertThat(inDb.get().getImageUrl()).isEqualTo(expectedImageUrl);
    }

    @Test
    @DisplayName("Should find nearby issues and map image URL correctly")
    void shouldFindNearbyIssues() {
        IssueEntity entity1 = IssueEntity.builder()
                .title("Issue 1")
                .description("Desc 1")
                .latitude(40.0).longitude(2.0)
                .reporterId(1L)
                .status(IssueStatus.REPORTED)
                .category(IssueCategory.OTHER)
                .imageUrl("http://img.com/issue1.jpg")
                .build();
        jpaRepository.save(entity1);

        List<UrbanIssue> result = adapter.findNearby(40.0, 2.0, 10.0, null, null, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle().value()).isEqualTo("Issue 1");
        assertThat(result.get(0).getImageUrl()).isEqualTo("http://img.com/issue1.jpg");
    }

    @Test
    @DisplayName("Should filter issues by Status and Category")
    void shouldFilterIssues() {
        jpaRepository.save(IssueEntity.builder()
                .title("Target Issue").latitude(40.0).longitude(2.0).reporterId(1L)
                .status(IssueStatus.IN_PROGRESS).category(IssueCategory.ROAD).build());

        jpaRepository.save(IssueEntity.builder()
                .title("Wrong Status").latitude(40.0).longitude(2.0).reporterId(2L)
                .status(IssueStatus.REPORTED).category(IssueCategory.ROAD).build());

        jpaRepository.save(IssueEntity.builder()
                .title("Wrong Category").latitude(40.0).longitude(2.0).reporterId(3L)
                .status(IssueStatus.IN_PROGRESS).category(IssueCategory.TRASH).build());

        List<UrbanIssue> result = adapter.findNearby(
                40.0, 2.0, 10.0,
                "IN_PROGRESS",
                "ROAD",
                0, 10
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle().value()).isEqualTo("Target Issue");
    }
}