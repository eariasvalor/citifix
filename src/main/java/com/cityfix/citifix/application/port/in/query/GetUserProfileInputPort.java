package com.cityfix.citifix.application.port.in.query;

import com.cityfix.citifix.domain.model.User;

public interface GetUserProfileInputPort {
    User execute(String email);
}