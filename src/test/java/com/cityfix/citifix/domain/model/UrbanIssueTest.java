package com.cityfix.citifix.domain.model;

import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class UrbanIssueTest {

    private UrbanIssue createBaseIssue() {
        return new UrbanIssue(
                1L,
                new IssueTitle("Original Title"),
                "Original Description",
                new Coordinates(41.3879, 2.1699),
                new UserId(100L),
                IssueStatus.REPORTED,
                IssueCategory.LIGHTING,
                "http://image.com/old.jpg",
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("Creation and Rehydration Tests")
    class CreationTests {
        @Test
        @DisplayName("Should throw exception if mandatory fields are null")
        void shouldValidateMandatoryFields() {
            assertThatThrownBy(() -> new UrbanIssue(null, null, "D", new Coordinates(0.0,0.0), new UserId(1L), null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Title is mandatory");
        }

        @Test
        @DisplayName("Should rehydrate correctly with 8 parameters (legacy support)")
        void shouldRehydrateWithLegacyMethod() {
            UrbanIssue issue = UrbanIssue.rehydrate(
                    1L, new IssueTitle("T"), "D", new Coordinates(0.0,0.0), new UserId(1L),
                    IssueStatus.REPORTED, IssueCategory.OTHER, null
            );
            assertThat(issue.getCreatedAt()).isNotNull();
            assertThat(issue.getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("Immutability & Evolution Tests")
    class EvolutionTests {

        @Test
        @DisplayName("Should return a new instance when details are updated")
        void updateDetailsShouldReturnNewInstance() {
            UrbanIssue original = createBaseIssue();
            UrbanIssue updated = original.updateDetails("New Title", "New Desc", IssueCategory.ROAD);

            // Assert New Instance
            assertThat(updated).isNotSameAs(original);
            assertThat(updated.getTitle().value()).isEqualTo("New Title");
            assertThat(updated.getCategory()).isEqualTo(IssueCategory.ROAD);

            // Assert Original Unchanged
            assertThat(original.getTitle().value()).isEqualTo("Original Title");
            assertThat(original.getCategory()).isEqualTo(IssueCategory.LIGHTING);
        }

        @Test
        @DisplayName("Should return a new instance when image URL is updated")
        void withImageUrlShouldReturnNewInstance() {
            UrbanIssue original = createBaseIssue();
            UrbanIssue updated = original.withImageUrl("http://image.com/new.jpg");

            assertThat(updated).isNotSameAs(original);
            assertThat(updated.getImageUrl()).isEqualTo("http://image.com/new.jpg");
            assertThat(original.getImageUrl()).isEqualTo("http://image.com/old.jpg");
        }

        @Test
        @DisplayName("Should maintain the same creation date across evolutions")
        void shouldMaintainCreationDate() {
            UrbanIssue original = createBaseIssue();
            UrbanIssue updated = original.markAsInProgress();

            assertThat(updated.getCreatedAt()).isEqualTo(original.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Status Transition Logic Tests")
    class StatusTests {

        @Test
        @DisplayName("Should transition to IN_PROGRESS correctly")
        void shouldTransitionToInProgress() {
            UrbanIssue issue = createBaseIssue();
            UrbanIssue inProgress = issue.markAsInProgress();

            assertThat(inProgress.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
            assertThat(issue.getStatus()).isEqualTo(IssueStatus.REPORTED);
        }

        @Test
        @DisplayName("Should throw exception when resolving a REPORTED issue directly")
        void shouldFailResolvingIfReported() {
            UrbanIssue issue = createBaseIssue();
            assertThatThrownBy(issue::resolve)
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("Should resolve an issue that is IN_PROGRESS")
        void shouldResolveCorrectly() {
            UrbanIssue inProgress = createBaseIssue().markAsInProgress();
            UrbanIssue resolved = inProgress.resolve();

            assertThat(resolved.getStatus()).isEqualTo(IssueStatus.RESOLVED);
        }

        @Test
        @DisplayName("Should not allow marking as in progress if already resolved")
        void shouldFailInProgressIfResolved() {
            UrbanIssue resolved = createBaseIssue().markAsInProgress().resolve();
            assertThatThrownBy(resolved::markAsInProgress)
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}