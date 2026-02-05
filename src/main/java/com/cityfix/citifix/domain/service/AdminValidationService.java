package com.cityfix.citifix.domain.service;

import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminValidationService {

    private final UserRepositoryPort userRepository;

    public boolean canRemoveAdminRole(Long userId) {
        long adminCount = userRepository.countByRole("ROLE_ADMIN");

        if (adminCount > 1) {
            return true;
        }

        return userRepository.findById(new UserId(userId))
                .map(user -> !user.getRoles().contains("ROLE_ADMIN"))
                .orElse(false);
    }
}