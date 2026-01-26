package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.domain.model.UrbanIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FindIssuesByUserIdInputPort {
    Page<UrbanIssue> execute(Long userId, String status, String category, Pageable pageable);
}
