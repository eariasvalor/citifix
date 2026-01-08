package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.FindNearbyIssuesInputPort;
import com.cityfix.citifix.application.port.in.query.FindNearbyIssuesQuery;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindNearbyIssuesUseCase implements FindNearbyIssuesInputPort {

    private final IssueRepositoryPort repositoryPort;

    @Override
    public List<UrbanIssue> execute(FindNearbyIssuesQuery query) {
        return repositoryPort.findNearby(
                query.latitude(),
                query.longitude(),
                query.radiusInMeters(),
                query.page(),
                query.size()
        );
    }
}