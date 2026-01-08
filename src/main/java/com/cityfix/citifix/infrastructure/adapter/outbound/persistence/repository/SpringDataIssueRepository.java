package com.cityfix.citifix.infrastructure.adapter.outbound.persistence.repository;

import com.cityfix.citifix.infrastructure.adapter.outbound.persistence.entity.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataIssueRepository extends JpaRepository<IssueEntity, Long> {

}