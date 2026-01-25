package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.domain.model.User;
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

        when(issueRepository.existsById(issueId)).thenReturn(true);

        User admin = mock(User.class);
        when(admin.getRoles()).thenReturn(Set.of("ROLE_ADMIN"));
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(admin));

        useCase.execute(issueId, adminEmail);

        verify(issueRepository).deleteById(issueId);
    }

    @Test
    @DisplayName("Should throw exception if requester is NOT ADMIN")
    void shouldThrowIfNotAdmin() {
        Long issueId = 100L;
        String userEmail = "citizen@cityfix.com";

        when(issueRepository.existsById(issueId)).thenReturn(true);

        User citizen = mock(User.class);
        when(citizen.getRoles()).thenReturn(Set.of("ROLE_USER"));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(citizen));

        assertThatThrownBy(() -> useCase.execute(issueId, userEmail))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Access denied: Only admins can delete issues");

        verify(issueRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw exception if issue does not exist")
    void shouldThrowIfNotFound() {
        when(issueRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(999L, "admin@cityfix.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Issue not found");

        verify(issueRepository, never()).deleteById(any());
    }
}