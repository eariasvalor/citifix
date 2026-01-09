package com.cityfix.citifix.domain.port.out;

import com.cityfix.citifix.domain.model.UrbanIssue;

import java.util.List;
import java.util.Optional;

public interface IssueRepositoryPort {
    UrbanIssue save(UrbanIssue issue);
    List<UrbanIssue> findNearby(Double lat, Double lon, Double radiusMeters, int page, int size);
    Optional<UrbanIssue> findById(Long id);
}