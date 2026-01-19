package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.mapper;

import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import org.springframework.stereotype.Component;

@Component
public class IssueMapper {

    public IssueEntity toEntity(UrbanIssue domain) {
        if (domain == null) return null;

        return IssueEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle().value())
                .description(domain.getDescription())
                .latitude(domain.getCoordinates().latitude())
                .longitude(domain.getCoordinates().longitude())
                .reporterId(domain.getReporterId().value())
                .status(domain.getStatus())
                .category(domain.getCategory())
                .build();
    }

    public UrbanIssue toDomain(IssueEntity entity) {
        if (entity == null) return null;

        return UrbanIssue.rehydrate(
                entity.getId(),
                new IssueTitle(entity.getTitle()),
                entity.getDescription(),
                new Coordinates(entity.getLatitude(), entity.getLongitude()),
                new UserId(entity.getReporterId()),
                entity.getStatus(),
                entity.getCategory());
    }
}