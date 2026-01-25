package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.command.UpdateUserCommand;
import com.cityfix.citifix.domain.model.User;

public interface UpdateUserInputPort {
    User execute(UpdateUserCommand command);
}
