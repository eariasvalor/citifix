package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.command.LoginCommand;

public interface LoginInputPort {
    String execute(LoginCommand command);
}