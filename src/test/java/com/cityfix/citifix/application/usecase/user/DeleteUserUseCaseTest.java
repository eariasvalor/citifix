package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    @Test
    @DisplayName("Should invoke repository delete method with correct UserId value object")
    void shouldDeleteUserSuccessfully() {
        Long rawId = 123L;
        UserId expectedUserId = new UserId(rawId);

        doNothing().when(userRepositoryPort).deleteById(expectedUserId);

        deleteUserUseCase.execute(rawId);

        verify(userRepositoryPort).deleteById(expectedUserId);
    }
}