package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.application.port.in.DeleteUserInputPort;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCase implements DeleteUserInputPort {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional
    public void execute(Long userId) {
        userRepositoryPort.deleteById(new UserId(userId));
    }
}
