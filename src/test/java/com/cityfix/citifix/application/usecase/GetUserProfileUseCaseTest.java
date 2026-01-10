package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.domain.model.User;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfileUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private GetUserProfileUseCase getUserProfileUseCase;

    @Test
    @DisplayName("Should return user when email exists")
    void shouldReturnUserWhenExists() {
        String email = "maria@cityfix.com";
        User expectedUser = User.create(email, "pass", Set.of("ROLE_ADMIN"));

        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        User result = getUserProfileUseCase.execute(email);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        verify(userRepositoryPort).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw exception when user does not exist")
    void shouldThrowExceptionWhenNotFound() {
        String email = "ghost@cityfix.com";
        when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getUserProfileUseCase.execute(email))
                .isInstanceOf(RuntimeException.class).hasMessage("User not found");
    }
}