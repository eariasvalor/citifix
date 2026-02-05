package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.domain.model.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FindAllUsersInputPort {
    Page<User> execute(int page, int size);
}
