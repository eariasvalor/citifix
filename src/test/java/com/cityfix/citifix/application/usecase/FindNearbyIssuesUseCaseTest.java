package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.query.FindNearbyIssuesQuery;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindNearbyIssuesUseCaseTest {

    @Mock
    private IssueRepositoryPort repositoryPort;

    @InjectMocks
    private FindNearbyIssuesUseCase useCase;

    @Test
    @DisplayName("Should return issues within the radius")
    void shouldReturnNearbyIssues() {
        var query = new FindNearbyIssuesQuery(41.38, 2.17, 500.0, 0, 10);

        var mockIssue = new UrbanIssue(1L, new IssueTitle("Test"), new Coordinates(41.38, 2.17), new UserId(1L));

        when(repositoryPort.findNearby(41.38, 2.17, 500.0, 0, 10))
                .thenReturn(List.of(mockIssue));

        List<UrbanIssue> result = useCase.execute(query);

        assertEquals(1, result.size());
        verify(repositoryPort).findNearby(41.38, 2.17, 500.0, 0, 10);
    }
}