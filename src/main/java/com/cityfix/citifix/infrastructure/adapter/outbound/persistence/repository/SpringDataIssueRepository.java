package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository;

import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface SpringDataIssueRepository extends JpaRepository<IssueEntity, Long> {
    @Query(value = """
        SELECT * FROM issues i 
        WHERE ST_Distance_Sphere(
            point(i.longitude, i.latitude), 
            point(:lon, :lat)
        ) <= :radius
        """, nativeQuery = true)
    List<IssueEntity> findNearby(
            @Param("lat") Double lat,
            @Param("lon") Double lon,
            @Param("radius") Double radius,
            Pageable pageable
    );

}