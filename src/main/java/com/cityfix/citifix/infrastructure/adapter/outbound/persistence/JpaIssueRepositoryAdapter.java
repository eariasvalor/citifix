package com.cityfix.citifix.infrastructure.adapter.outbound.persistence;

import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.mapper.IssueMapper;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository.SpringDataIssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    public List<UrbanIssue> findNearby(Double latitude, Double longitude, Double radiusInMeters, String status, String category, Integer page, Integer size) {
        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 10;
        Double radiusInKm = (radiusInMeters != null) ? radiusInMeters / 1000.0 : 5.0;

        IssueStatus statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = IssueStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
            }
        }

        IssueCategory categoryEnum = null;
        if (category != null && !category.isBlank()) {
            try {
                categoryEnum = IssueCategory.valueOf(category);
            } catch (IllegalArgumentException e) {
            }
        }

        var entities = repository.findNearby(
                latitude,
                longitude,
                radiusInKm,
                status,
                category,
                PageRequest.of(p, s)
        );

        return entities.stream()
                .map(issueMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Page<UrbanIssue> findByReporterIdWithFilters(Long userId, String status, String category, Pageable pageable) {
        Specification<IssueEntity> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("reporterId"), userId));

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status.toUpperCase()));
            }

            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.get("category"), category.toUpperCase()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return repository.findAll(spec, pageable).map(issueMapper::toDomain);
    }

}