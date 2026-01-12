package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;

public interface CreateIssueInputPort {
    Long execute(CreateIssueCommand command);
}