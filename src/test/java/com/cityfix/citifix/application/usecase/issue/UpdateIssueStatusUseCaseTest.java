package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateIssueStatusUseCaseTest {

    @Mock
    private IssueRepositoryPort repositoryPort;

    @InjectMocks
    private UpdateIssueStatusUseCase useCase;

    @Test
    @DisplayName("Should update status to IN_PROGRESS and save")
    void shouldUpdateStatusToInProgress() {
            Long issueId = 1L;
            var command = new UpdateIssueStatusCommand(issueId, "IN_PROGRESS");
            var issue = createMockIssue(issueId, IssueStatus.REPORTED);

            when(repositoryPort.findById(issueId)).thenReturn(Optional.of(issue));
            when(repositoryPort.save(any(UrbanIssue.class))).thenAnswer(i -> i.getArgument(0));

            UrbanIssue result = useCase.execute(command);

            assertEquals(IssueStatus.IN_PROGRESS, result.getStatus());
            verify(repositoryPort).save(argThat(u -> u.getStatus() == IssueStatus.IN_PROGRESS));
    }

    @Test
    @DisplayName("Should throw exception if issue not found")
    void shouldThrowExceptionIfNotFound() {
        var command = new UpdateIssueStatusCommand(999L, "IN_PROGRESS");
        when(repositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));
        verify(repositoryPort, never()).save(any());
    }


    private UrbanIssue createMockIssue(Long id, IssueStatus status) {
        return new UrbanIssue(
                id,
                new IssueTitle("Test Title"),
                "Description",
                new Coordinates(0.0, 0.0),
                new UserId(1L),
                status,
                IssueCategory.OTHER,
                null,
                LocalDateTime.now()
        );
    }
}