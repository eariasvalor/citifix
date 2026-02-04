package com.cityfix.citifix.application.usecase.issue;

import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FindIssueByIdUseCaseTest {

    @Mock
    private IssueRepositoryPort issueRepository;

    @InjectMocks
    private FindIssueByIdUseCase useCase;

    @Test
    @DisplayName("It must return the issue when the ID exists")
    void shouldReturnIssueWhenIdExists() {
        Long issueId = 1L;
        UrbanIssue mockIssue = UrbanIssue.rehydrate(
                issueId,
                new IssueTitle("Porthole in the street"),
                "Description",
                new Coordinates(40.0, -3.0),
                new UserId(10L),
                IssueStatus.REPORTED,
                IssueCategory.ROAD,
                null
        );
        given(issueRepository.findById(issueId)).willReturn(Optional.of(mockIssue));

        Optional<UrbanIssue> result = useCase.execute(issueId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(issueId);
        verify(issueRepository).findById(issueId);
    }

    @Test
    @DisplayName("It must return empty when the ID does not exist")
    void shouldReturnEmptyWhenIdDoesNotExist() {
        Long issueId = 999L;
        given(issueRepository.findById(issueId)).willReturn(Optional.empty());

        Optional<UrbanIssue> result = useCase.execute(issueId);

        assertThat(result).isEmpty();
        verify(issueRepository).findById(issueId);
    }
}