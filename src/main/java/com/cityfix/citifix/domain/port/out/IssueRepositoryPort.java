package com.cityfix.citifix.domain.port.out;

import com.cityfix.citifix.domain.model.GlobalStats;
import com.cityfix.citifix.domain.model.UrbanIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IssueRepositoryPort {

    UrbanIssue save(UrbanIssue issue);
    Optional<UrbanIssue> findById(Long id);
    List<UrbanIssue> findNearby(Double latitude, Double longitude, Double radius, String status, String category, Integer page, Integer size);
    boolean existsById(Long id);
    void deleteById(Long id);
    Page<UrbanIssue> findByReporterIdWithFilters(Long userId, String status, String category, Pageable pageable);
    Page<UrbanIssue> findAll(Pageable pageable);
    GlobalStats getGlobalStats();

}