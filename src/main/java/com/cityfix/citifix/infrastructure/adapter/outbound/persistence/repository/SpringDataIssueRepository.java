package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository;

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
}