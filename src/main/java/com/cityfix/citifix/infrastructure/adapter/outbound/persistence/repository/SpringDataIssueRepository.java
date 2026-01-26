package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository;

import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataIssueRepository extends JpaRepository<IssueEntity, Long>, JpaSpecificationExecutor<IssueEntity> {

    @Query(value = """
        SELECT * FROM urban_issues i
        WHERE (6371 * acos(LEAST(1.0, 
                cos(radians(:lat)) * cos(radians(i.latitude)) *
                cos(radians(i.longitude) - radians(:lon)) +
                sin(radians(:lat)) * sin(radians(i.latitude))
        ))) <= :radius
        AND (CAST(:status AS VARCHAR) IS NULL OR i.status = CAST(:status AS VARCHAR))
        AND (CAST(:category AS VARCHAR) IS NULL OR i.category = CAST(:category AS VARCHAR))
        """, nativeQuery = true)
    List<IssueEntity> findNearby(
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("radius") Double radiusInKm,
            @Param("status") String status,
            @Param("category") String category,
            Pageable pageable
    );

    List<IssueEntity> findByReporterId(Long reporterId, Pageable pageable);


}