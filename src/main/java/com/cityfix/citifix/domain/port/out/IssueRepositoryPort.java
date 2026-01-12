package com.cityfix.citifix.domain.port.out;

import com.cityfix.citifix.domain.model.UrbanIssue;
import java.util.List;
import java.util.Optional;

public interface IssueRepositoryPort {

    UrbanIssue save(UrbanIssue issue);

    Optional<UrbanIssue> findById(Long id);

    List<UrbanIssue> findNearby(Double latitude, Double longitude, Double radiusInKm, Integer page, Integer size);
}