package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.application.port.in.command.CreateUserCommand;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("Should create user successfully when email is unique")
    void shouldCreateUserWhenEmailUnique() {
        CreateUserCommand command = new CreateUserCommand("newuser@cityfix.com", "plainPassword", "ROLE_USER");

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("encodedHash123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = createUserUseCase.execute(command);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(command.email());
        assertThat(createdUser.getPassword()).isEqualTo("encodedHash123");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getRoles()).contains("ROLE_USER");
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowWhenEmailExists() {
        CreateUserCommand command = new CreateUserCommand("existing@cityfix.com", "pass", "ROLE_USER");

        when(userRepository.existsByEmail(command.email())).thenReturn(true);

        assertThatThrownBy(() -> createUserUseCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already in use");

        verify(userRepository, never()).save(any());
    }
}