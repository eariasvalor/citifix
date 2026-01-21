package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateIssueUseCaseTest {

    @Mock
    private IssueRepositoryPort issueRepositoryPort;

    @InjectMocks
    private UpdateIssueUseCase updateIssueUseCase;

    @Test
    @DisplayName("Should update issue details and status successfully")
    void shouldUpdateIssueSuccessfully() {
        Long issueId = 1L;
        UrbanIssue existingIssue = UrbanIssue.rehydrate(
                issueId,
                new IssueTitle("Old Title"),
                "Old Desc",
                new Coordinates(1.0, 1.0),
                new UserId(10L),
                IssueStatus.REPORTED,
                IssueCategory.OTHER
        );

        UpdateIssueCommand command = new UpdateIssueCommand(
                issueId,
                "New Title",
                "New Description",
                "IN_PROGRESS",
                "ROAD"
        );

        when(issueRepositoryPort.findById(issueId)).thenReturn(Optional.of(existingIssue));
        when(issueRepositoryPort.save(any(UrbanIssue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrbanIssue result = updateIssueUseCase.execute(command);

        assertThat(result.getTitle().value()).isEqualTo("New Title");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
        assertThat(result.getCategory()).isEqualTo(IssueCategory.ROAD);

        verify(issueRepositoryPort).save(existingIssue);
    }

    @Test
    @DisplayName("Should throw exception when issue not found")
    void shouldThrowExceptionWhenIssueNotFound() {
        UpdateIssueCommand command = new UpdateIssueCommand(1L, "T", "D", "OPEN", "ROAD");
        when(issueRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateIssueUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Issue not found");

        verify(issueRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should only update non-null fields")
    void shouldUpdateOnlyNonNullFields() {
        Long issueId = 1L;
        UrbanIssue existingIssue = UrbanIssue.rehydrate(
                issueId,
                new IssueTitle("Original Title"),
                "Original Desc",
                new Coordinates(1.0, 1.0),
                new UserId(10L),
                IssueStatus.REPORTED,
                IssueCategory.OTHER
        );

        UpdateIssueCommand command = new UpdateIssueCommand(
                issueId,
                null,
                "New Desc",
                null,
                null
        );

        when(issueRepositoryPort.findById(issueId)).thenReturn(Optional.of(existingIssue));
        when(issueRepositoryPort.save(any(UrbanIssue.class))).thenAnswer(i -> i.getArgument(0));

        UrbanIssue result = updateIssueUseCase.execute(command);

        assertThat(result.getTitle().value()).isEqualTo("Original Title");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.getStatus()).isEqualTo(IssueStatus.REPORTED);
    }
}