package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.application.port.in.UpdateUserInputPort;
import com.cityfix.citifix.application.port.in.command.UpdateUserCommand;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase implements UpdateUserInputPort {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional
    public User execute(UpdateUserCommand command) {
        User user = userRepositoryPort.findById(new UserId(command.userId()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User updatedUser = user.updateDetails(command.email(), command.roles());

        return userRepositoryPort.save(updatedUser);
    }
}