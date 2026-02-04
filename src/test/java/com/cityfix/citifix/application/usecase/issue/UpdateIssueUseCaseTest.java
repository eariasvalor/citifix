package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.DomainEventPublisherPort;
import com.cityfix.citifix.domain.port.out.ImageStoragePort;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Set;

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

    @Mock
    private DomainEventPublisherPort eventPublisher;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UpdateIssueUseCase updateIssueUseCase;

    private final String TEST_EMAIL = "user@test.com";
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User(new UserId(10L), TEST_EMAIL, "hash", Set.of("ROLE_USER"));
    }

    @Test
    @DisplayName("Should update issue details successfully when requester is the owner")
    void shouldUpdateIssueSuccessfully() throws IOException {
        Long issueId = 1L;
        UrbanIssue existingIssue = createBaseIssue(issueId, 10L);

        UpdateIssueCommand command = new UpdateIssueCommand(
                issueId, "New Title", "New Description", null, "ROAD", null
        );

        when(issueRepositoryPort.findById(issueId)).thenReturn(Optional.of(existingIssue));
        when(userRepositoryPort.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(issueRepositoryPort.save(any(UrbanIssue.class))).thenAnswer(i -> i.getArgument(0));

        UrbanIssue result = updateIssueUseCase.execute(command, TEST_EMAIL);

        assertThat(result.getTitle().value()).isEqualTo("New Title");
        verify(issueRepositoryPort).save(any(UrbanIssue.class));
    }

    @Test
    @DisplayName("Should upload image when provided by owner")
    void shouldUpdateImageWhenProvided() throws IOException {
        Long issueId = 1L;
        UrbanIssue existingIssue = createBaseIssue(issueId, 10L);

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(imageStoragePort.upload(mockFile)).thenReturn("http://new-image.url");

        UpdateIssueCommand command = new UpdateIssueCommand(issueId, null, null, null, null, mockFile);

        when(issueRepositoryPort.findById(issueId)).thenReturn(Optional.of(existingIssue));
        when(userRepositoryPort.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(issueRepositoryPort.save(any(UrbanIssue.class))).thenAnswer(i -> i.getArgument(0));

        UrbanIssue result = updateIssueUseCase.execute(command, TEST_EMAIL);

        assertThat(result.getImageUrl()).isEqualTo("http://new-image.url");
    }

    @Test
    @DisplayName("Should throw exception when issue not found")
    void shouldThrowExceptionWhenIssueNotFound() {
        UpdateIssueCommand command = new UpdateIssueCommand(1L, "T", "D", null, "ROAD", null);
        when(issueRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateIssueUseCase.execute(command, TEST_EMAIL))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw SecurityException when requester is not the owner")
    void shouldThrowWhenNotOwner() {
        Long issueId = 1L;
        UrbanIssue existingIssue = createBaseIssue(issueId, 999L);

        UpdateIssueCommand command = new UpdateIssueCommand(issueId, "Title", null, null, null, null);

        when(issueRepositoryPort.findById(issueId)).thenReturn(Optional.of(existingIssue));
        when(userRepositoryPort.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> updateIssueUseCase.execute(command, TEST_EMAIL))
                .isInstanceOf(SecurityException.class);
    }

    private UrbanIssue createBaseIssue(Long id, Long reporterId) {
        return new UrbanIssue(
                id,
                new IssueTitle("Original Title"),
                "Original Desc",
                new Coordinates(1.0, 1.0),
                new UserId(reporterId),
                IssueStatus.REPORTED,
                IssueCategory.OTHER,
                null,
                LocalDateTime.now()
        );
    }
}