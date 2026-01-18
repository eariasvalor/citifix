package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.mapper.IssueMapper;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.SpringDataIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaIssueRepositoryAdapter implements IssueRepositoryPort {

    private final SpringDataIssueRepository repository;
    private final IssueMapper issueMapper;

    @Override
    public UrbanIssue save(UrbanIssue issue) {
        var entity = issueMapper.toEntity(issue);
        var savedEntity = repository.save(entity);
        return issueMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UrbanIssue> findById(Long id) {
        return repository.findById(id)
                .map(issueMapper::toDomain);
    }

    @Override
    public List<UrbanIssue> findNearby(Double latitude, Double longitude, Double radiusInMeters, Integer page, Integer size) {
        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 10;

        Double radiusInKm = (radiusInMeters != null) ? radiusInMeters / 1000.0 : 5.0;

        var entities = repository.findNearby(
                latitude,
                longitude,
                radiusInKm,
                PageRequest.of(p, s)
        );

        return entities.stream()
                .map(issueMapper::toDomain)
                .collect(Collectors.toList());
    }
}