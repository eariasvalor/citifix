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
import org.springframework.stereotype.Component;

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
}