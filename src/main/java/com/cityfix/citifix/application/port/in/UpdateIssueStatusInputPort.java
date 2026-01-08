package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;

public interface UpdateIssueStatusInputPort {
    void execute(UpdateIssueStatusCommand command);
}