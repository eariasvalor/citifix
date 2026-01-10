package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.CreateUserInputPort;
import com.cityfix.citifix.application.port.in.command.CreateUserCommand;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase implements CreateUserInputPort {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User execute(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        String roleName = (command.role() == null || command.role().isBlank())
                ? "ROLE_USER"
                : command.role();

        var user = User.create(
                command.email(),
                passwordEncoder.encode(command.password()),
                Set.of(roleName)
        );

        return userRepository.save(user);
    }
}