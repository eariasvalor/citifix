package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.SpringDataIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaIssueRepositoryAdapter implements IssueRepositoryPort {

    private final SpringDataIssueRepository springRepository;

    @Override
    public UrbanIssue save(UrbanIssue issue) {
        IssueEntity entity = new IssueEntity();
        entity.setId(issue.getId());
        entity.setTitle(issue.getTitle().value());
        entity.setLatitude(issue.getCoordinates().latitude());
        entity.setLongitude(issue.getCoordinates().longitude());
        entity.setReporterId(issue.getReporterId().value());
        entity.setStatus(issue.getStatus().name());

        IssueEntity savedEntity = springRepository.save(entity);

        return UrbanIssue.rehydrate(
                savedEntity.getId(),
                new IssueTitle(savedEntity.getTitle()),
                new Coordinates(savedEntity.getLatitude(), savedEntity.getLongitude()),
                new UserId(savedEntity.getReporterId()),
                IssueStatus.valueOf(savedEntity.getStatus())
        );
    }

    @Override
    public List<UrbanIssue> findNearby(Double lat, Double lon, Double radiusMeters, int page, int size) {


        var pageable = PageRequest.of(page, size);

        return springRepository.findNearby(lat, lon, radiusMeters, pageable)
                .stream()
                .map(entity -> UrbanIssue.rehydrate(
                        entity.getId(),
                        new IssueTitle(entity.getTitle()),
                        new Coordinates(entity.getLatitude(), entity.getLongitude()),
                        new UserId(entity.getReporterId()),
                        IssueStatus.valueOf(entity.getStatus())
                ))
                .toList();
    }

    @Override
    public Optional<UrbanIssue> findById(Long id) {
        return springRepository.findById(id)
                .map(entity -> UrbanIssue.rehydrate(
                        entity.getId(),
                        new IssueTitle(entity.getTitle()),
                        new Coordinates(entity.getLatitude(), entity.getLongitude()),
                        new UserId(entity.getReporterId()),
                        IssueStatus.valueOf(entity.getStatus())
                ));
    }
}