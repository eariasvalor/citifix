package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.domain.model.User;

import java.util.List;

public interface FindAllUsersInputPort {
    List<User> execute(int page, int size);
}
