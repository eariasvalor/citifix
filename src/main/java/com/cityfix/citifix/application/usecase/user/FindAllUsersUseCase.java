package com.cityfix.citifix.application.usecase.user;

import com.cityfix.citifix.application.port.in.FindAllUsersInputPort;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindAllUsersUseCase implements FindAllUsersInputPort {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public Page<User> execute(int page, int size) {
        return userRepositoryPort.findAll(page, size);
    }
}