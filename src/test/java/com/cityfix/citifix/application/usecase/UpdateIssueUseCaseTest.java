package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.ImageStoragePort;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateIssueUseCaseTest {

    @Mock
    private IssueRepositoryPort issueRepositoryPort;

    @Mock
    private ImageStoragePort imageStoragePort;

    @InjectMocks
    private UpdateIssueUseCase updateIssueUseCase;

    @Test
    @DisplayName("Should update issue details and status successfully generating a new instance")
    void shouldUpdateIssueSuccessfully() throws IOException {
        Long issueId = 1L;
        UrbanIssue existingIssue = new UrbanIssue(
                issueId,
                new IssueTitle("Old Title"),
                "Old Desc",
                new Coordinates(1.0, 1.0),
                new UserId(10L),
                IssueStatus.REPORTED,
                IssueCategory.OTHER,
                null,
                LocalDateTime.now()
        );

        UpdateIssueCommand command = new UpdateIssueCommand(
                issueId,
                "New Title",
                "New Description",
                "IN_PROGRESS",
                "ROAD",
                null
        );

        when(issueRepositoryPort.findById(issueId)).thenReturn(Optional.of(existingIssue));
        when(issueRepositoryPort.save(any(UrbanIssue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UrbanIssue result = updateIssueUseCase.execute(command);

        assertThat(result.getTitle().value()).isEqualTo("New Title");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
        assertThat(result.getCategory()).isEqualTo(IssueCategory.ROAD);
        verify(issueRepositoryPort).save(any(UrbanIssue.class));
    }

    @Test
    @DisplayName("Should upload image and update URL when provided")
    void shouldUpdateImageWhenProvided() throws IOException {
        Long issueId = 1L;
        UrbanIssue existingIssue = new UrbanIssue(issueId, new IssueTitle("T"), "D", new Coordinates(1.0,1.0), new UserId(1L), IssueStatus.REPORTED, IssueCategory.OTHER, null, LocalDateTime.now());

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(imageStoragePort.upload(mockFile)).thenReturn("http://new-image.url");

        UpdateIssueCommand command = new UpdateIssueCommand(issueId, null, null, null, null, mockFile);

        when(issueRepositoryPort.findById(issueId)).thenReturn(Optional.of(existingIssue));
        when(issueRepositoryPort.save(any(UrbanIssue.class))).thenAnswer(i -> i.getArgument(0));

        UrbanIssue result = updateIssueUseCase.execute(command);

        assertThat(result.getImageUrl()).isEqualTo("http://new-image.url");
        verify(imageStoragePort).upload(mockFile);
    }

    @Test
    @DisplayName("Should throw exception when issue not found")
    void shouldThrowExceptionWhenIssueNotFound() {
        UpdateIssueCommand command = new UpdateIssueCommand(1L, "T", "D", "REPORTED", "ROAD", null);
        when(issueRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateIssueUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Issue not found");

        verify(issueRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should only update non-null fields returning a new version")
    void shouldUpdateOnlyNonNullFields() throws IOException {
        Long issueId = 1L;
        UrbanIssue existingIssue = new UrbanIssue(
                issueId,
                new IssueTitle("Original Title"),
                "Original Desc",
                new Coordinates(1.0, 1.0),
                new UserId(10L),
                IssueStatus.REPORTED,
                IssueCategory.OTHER,
                null,
                LocalDateTime.now()
        );

        UpdateIssueCommand command = new UpdateIssueCommand(
                issueId,
                null,
                "New Desc",
                null,
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