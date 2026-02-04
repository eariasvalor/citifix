package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteIssueUseCaseTest {

    @Mock private IssueRepositoryPort issueRepository;
    @Mock private UserRepositoryPort userRepository;

    @InjectMocks private DeleteIssueUseCase useCase;

    @Test
    @DisplayName("Should delete issue if requester has ROLE_ADMIN")
    void shouldDeleteIfAdmin() {
        Long issueId = 100L;
        String adminEmail = "admin@cityfix.com";

        UrbanIssue mockIssue = mock(UrbanIssue.class);
        when(mockIssue.getReporterId()).thenReturn(new UserId(999L));
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(mockIssue));

        User admin = mock(User.class);
        when(admin.getId()).thenReturn(new UserId(1L));
        when(admin.getRoles()).thenReturn(Set.of("ROLE_ADMIN"));
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(admin));

        useCase.execute(issueId, adminEmail);

        verify(issueRepository).deleteById(issueId);
    }

    @Test
    @DisplayName("Should throw SecurityException if requester is NOT ADMIN and NOT OWNER")
    void shouldThrowIfNotAdmin() {

        Long issueId = 100L;
        String userEmail = "citizen@cityfix.com";
        UserId reporterId = new UserId(999L);
        UserId requesterId = new UserId(1L);

        UrbanIssue mockIssue = mock(UrbanIssue.class);
        when(mockIssue.getReporterId()).thenReturn(reporterId);
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(mockIssue));

        User citizen = mock(User.class);
        when(citizen.getId()).thenReturn(requesterId);
        when(citizen.getRoles()).thenReturn(Set.of("ROLE_USER"));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(citizen));

        assertThatThrownBy(() -> useCase.execute(issueId, userEmail))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Access denied");

        verify(issueRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw exception if issue does not exist")
    void shouldThrowIfNotFound() {
        Long issueId = 999L;
        String email = "admin@cityfix.com";

        when(issueRepository.findById(issueId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(issueId, email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Issue not found");

        verify(issueRepository, never()).deleteById(any());
    }
}