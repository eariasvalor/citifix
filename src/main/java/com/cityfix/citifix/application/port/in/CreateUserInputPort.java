package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.command.CreateUserCommand;
import com.cityfix.citifix.domain.model.User;

public interface CreateUserInputPort {
    User execute(CreateUserCommand command);
}