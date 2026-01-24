package com.cityfix.citifix.application.port.in;

public interface DeleteIssueInputPort {
    void execute (Long issueId, String requesterEmail);
}
