package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.domain.model.UrbanIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllIssuesInputPort {
    Page<UrbanIssue> execute(Pageable pageable);
}