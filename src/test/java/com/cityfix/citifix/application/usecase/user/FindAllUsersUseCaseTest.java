package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindAllUsersUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private FindAllUsersUseCase findAllUsersUseCase;

    @Test
    @DisplayName("Should return a list of all users from the repository")
    void shouldReturnListOfUsers() {
        User user1 = new User(1L, "admin@cityfix.com", "hash1", Set.of("ADMIN"));
        User user2 = new User(2L, "citizen@cityfix.com", "hash2", Set.of("USER"));
        List<User> expectedUsers = List.of(user1, user2);

        when(userRepositoryPort.findAll()).thenReturn(expectedUsers);

        List<User> result = findAllUsersUseCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(user1, user2);
        verify(userRepositoryPort).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyList() {
        when(userRepositoryPort.findAll()).thenReturn(List.of());

        List<User> result = findAllUsersUseCase.execute();

        assertThat(result).isEmpty();
        verify(userRepositoryPort).findAll();
    }
}