package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.domain.model.UrbanIssue;
import java.util.Optional;

public interface FindIssueByIdInputPort {
    Optional<UrbanIssue> execute(Long id);
}