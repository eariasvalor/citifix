package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository;

import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataIssueRepository extends JpaRepository<IssueEntity, Long> {

    @Query(value = """
        SELECT * FROM urban_issues i
        WHERE (6371 * acos(LEAST(1.0, 
                cos(radians(:lat)) * cos(radians(i.latitude)) *
                cos(radians(i.longitude) - radians(:lon)) +
                sin(radians(:lat)) * sin(radians(i.latitude))
        ))) <= :radius
        """, nativeQuery = true)
    List<IssueEntity> findNearby(
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("radius") Double radiusInKm, Pageable pageable
    );

    @Query("""
        SELECT i FROM IssueEntity i 
        WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(i.latitude)) * cos(radians(i.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(i.latitude)))) < :radius
        AND (:status IS NULL OR i.status = :status)
        AND (:category IS NULL OR i.category = :category)
    """)
    List<IssueEntity> findNearbyWithFilters(
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("radius") Double radius,
            @Param("status") IssueStatus status,
            @Param("category") IssueCategory category,
            Pageable pageable
    );
}