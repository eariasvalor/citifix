package com.cityfix.citifix.domain.port.out;

import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.valueobject.UserId;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    User save(User user);
    boolean existsByEmail(String email);
    long count();
    Optional<User> findById(UserId id);
}