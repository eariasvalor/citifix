package com.cityfix.citifix.domain.service;

import com.cityfix.citifix.domain.model.enums.IssueStatus;

public interface ImpactRewardPolicy {

    long calculatePoints(String oldStatus, String newStatus);
}