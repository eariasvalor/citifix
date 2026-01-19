package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.GetUserProfileInputPort;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserProfileUseCase implements GetUserProfileInputPort {

    private final UserRepositoryPort userRepository;

    @Override
    public User execute(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}