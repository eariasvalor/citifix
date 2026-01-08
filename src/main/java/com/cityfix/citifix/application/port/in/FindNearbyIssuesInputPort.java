package com.cityfix.citifix.application.port.in;

import com.cityfix.citifix.application.port.in.query.FindNearbyIssuesQuery;
import com.cityfix.citifix.domain.model.UrbanIssue;
import java.util.List;

public interface FindNearbyIssuesInputPort {
    List<UrbanIssue> execute(FindNearbyIssuesQuery query);
}