package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.application.port.in.command.UpdateUserCommand;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private UpdateUserUseCase useCase;

    @Test
    @DisplayName("Should update user email and roles, returning a new instance")
    void shouldUpdateUserSuccessfully() {

        UserId userId = new UserId(1L);
        User existingUser = new User(userId, "old@cityfix.com", "encoded_pass", Set.of("USER"));
        UpdateUserCommand command = new UpdateUserCommand(userId.value(), "new@cityfix.com", Set.of("ADMIN"));

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = useCase.execute(command);

        assertThat(result.getEmail()).isEqualTo("new@cityfix.com");
        assertThat(result.getRoles()).containsExactly("ADMIN");
        verify(userRepositoryPort).save(argThat(u -> u.getEmail().equals("new@cityfix.com")));
    }

    @Test
    @DisplayName("Should update user email and keep original roles when roles are null")
    void shouldUpdateUserPartially() {
        Long rawId = 1L;
        UserId userId = new UserId(rawId);
        User existingUser = new User(userId, "old@cityfix.com", "hash", Set.of("USER"));
        UpdateUserCommand command = new UpdateUserCommand(rawId, "new@cityfix.com", null);

        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepositoryPort.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = useCase.execute(command);

        assertEquals("new@cityfix.com", result.getEmail());
        assertEquals(Set.of("USER"), result.getRoles());
        verify(userRepositoryPort).save(argThat(u -> u.getEmail().equals("new@cityfix.com")));
    }
}