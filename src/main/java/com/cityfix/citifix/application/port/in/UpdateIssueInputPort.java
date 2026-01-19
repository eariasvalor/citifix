package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;

public interface UpdateIssueInputPort {
    UrbanIssue execute(UpdateIssueCommand command);
}
