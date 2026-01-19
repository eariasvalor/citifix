package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;

public interface UpdateIssueStatusInputPort {
    UrbanIssue execute(UpdateIssueStatusCommand command);
}