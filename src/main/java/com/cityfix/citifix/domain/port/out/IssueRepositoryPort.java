package com.cityfix.citifix.domain.port.out;

import com.cityfix.citifix.domain.model.UrbanIssue;

public interface IssueRepositoryPort {
    UrbanIssue save(UrbanIssue issue);
}