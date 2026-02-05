package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindAllUsersUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private FindAllUsersUseCase findAllUsersUseCase;

    @Test
    @DisplayName("Should return a page of all users from the repository")
    void shouldReturnListOfUsers() {
        User user1 = new User(1L, "admin@cityfix.com", "hash1", Set.of("ADMIN"));
        User user2 = new User(2L, "citizen@cityfix.com", "hash2", Set.of("USER"));
        List<User> expectedUsers = List.of(user1, user2);

        Page<User> expectedPage = new PageImpl<>(expectedUsers, PageRequest.of(0, 10), expectedUsers.size());

        when(userRepositoryPort.findAll(0, 10)).thenReturn(expectedPage);

        Page<User> result = findAllUsersUseCase.execute(0, 10);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(user1, user2);
        verify(userRepositoryPort).findAll(0, 10);
    }


    @Test
    @DisplayName("Should return empty list when no users exist")
    void shouldReturnEmptyList() {
        Page<User> emptyPage = new PageImpl<>(List.of(), PageRequest.of(1, 10), 0);

        when(userRepositoryPort.findAll(1, 10)).thenReturn(emptyPage);

        Page<User> result = findAllUsersUseCase.execute(1, 10);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(userRepositoryPort).findAll(1, 10);
    }
}