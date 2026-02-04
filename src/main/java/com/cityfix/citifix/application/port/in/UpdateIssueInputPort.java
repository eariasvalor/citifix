package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;

import java.io.IOException;

public interface UpdateIssueInputPort {
    UrbanIssue execute(UpdateIssueCommand command, String requesterEmail) throws IOException;
}
