package com.cityfix.citifix.application.usecase;

import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateIssueUseCaseTest {

    @Mock
    private IssueRepositoryPort repositoryPort;
    @InjectMocks
    private CreateIssueUseCase useCase;

    @Test
    @DisplayName("Should create an issue, persist it, and return the result")
    void shouldCreateAndPersistIssue() {
        CreateIssueCommand command = new CreateIssueCommand(
                "Broken Light",
                41.38,
                2.17,
                100L
        );

        when(repositoryPort.save(any(UrbanIssue.class))).thenAnswer(invocation -> {
            UrbanIssue argument = invocation.getArgument(0);
            argument.setId(1L);
            return argument;
        });

        UrbanIssue result = useCase.execute(command);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Broken Light", result.getTitle().value());
        assertEquals(IssueStatus.REPORTED, result.getStatus());

        verify(repositoryPort).save(any(UrbanIssue.class));
    }
}