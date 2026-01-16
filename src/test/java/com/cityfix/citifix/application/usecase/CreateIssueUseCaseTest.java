package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateIssueUseCaseTest {

    @Mock
    private IssueRepositoryPort issueRepository;
    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private CreateIssueUseCase createIssueUseCase;

    @Test
    @DisplayName("Should create issue successfully when user exists")
    void shouldCreateIssueWhenUserExists() {
        String email = "citizen@cityfix.com";
        CreateIssueCommand command = new CreateIssueCommand("Broken Lamp", 41.38, 2.17, email);

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(99L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        when(issueRepository.save(any(UrbanIssue.class))).thenAnswer(invocation -> {
            UrbanIssue argument = invocation.getArgument(0);
            return UrbanIssue.rehydrate(
                    1L, argument.getTitle(),
                    argument.getCoordinates(),
                    argument.getReporterId(),
                    IssueStatus.REPORTED
            );
        });

        UrbanIssue issueId = createIssueUseCase.execute(command);

        assertThat(issueId).isEqualTo(1L);

        ArgumentCaptor<UrbanIssue> captor = ArgumentCaptor.forClass(UrbanIssue.class);
        verify(issueRepository).save(captor.capture());

        UrbanIssue capturedIssue = captor.getValue();
        assertThat(capturedIssue.getTitle().value()).isEqualTo("Broken Lamp");
        assertThat(capturedIssue.getCoordinates().latitude()).isEqualTo(41.38);
        assertThat(capturedIssue.getReporterId().value()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowWhenUserNotFound() {
        CreateIssueCommand command = new CreateIssueCommand("Title", 1.0, 1.0, "ghost@cityfix.com");
        when(userRepository.findByEmail(command.reporterEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createIssueUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Reporter user not found");

        verify(issueRepository, never()).save(any());
    }
}