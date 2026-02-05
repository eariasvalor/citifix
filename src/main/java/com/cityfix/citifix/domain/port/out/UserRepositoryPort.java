package com.cityfix.citifix.domain.port.out;

import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    User save(User user);
    boolean existsByEmail(String email);
    long count();
    Optional<User> findById(UserId id);
    void deleteById(UserId id);
    Page<User> findAll(int page, int size);
    long countByRole(String role);
}