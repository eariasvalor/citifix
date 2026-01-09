package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.CreateIssueInputPort;
import com.cityfix.citifix.application.port.in.FindNearbyIssuesInputPort;
import com.cityfix.citifix.application.port.in.UpdateIssueStatusInputPort;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.application.port.in.query.FindNearbyIssuesQuery;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.CreateIssueRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.UpdateStatusRequest;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IssueController.class)
@AutoConfigureMockMvc(addFilters = false)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateIssueInputPort createIssueInputPort;

    @MockBean
    private FindNearbyIssuesInputPort findNearbyInputPort;

    @MockBean
    private UpdateIssueStatusInputPort updateStatusInputPort;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Should create issue via REST API and return 201 Created")
    void shouldCreateIssue() throws Exception {
        var request = new CreateIssueRequest("Dangerous pothole", 41.38, 2.17);

        var mockIssue = new UrbanIssue(
                1L,
                new IssueTitle("Dangerous pothole"),
                new Coordinates(41.38, 2.17),
                new UserId(100L)
        );

        when(createIssueInputPort.execute(any(CreateIssueCommand.class))).thenReturn(mockIssue);

        mockMvc.perform(post("/api/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Dangerous pothole"))
                .andExpect(jsonPath("$.status").value("REPORTED"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when JSON fields are invalid")
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        var invalidRequest = new CreateIssueRequest(null, 500.0, 500.0);
        mockMvc.perform(post("/api/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.type").value("VALIDATION_ERROR")).andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("Should return 422 Unprocessable Entity when Domain Logic fails")
    void shouldReturn422WhenDomainFails() throws Exception {
        var request = new CreateIssueRequest("Title", 41.0, 2.0);

        when(createIssueInputPort.execute(any(CreateIssueCommand.class)))
                .thenThrow(new IllegalArgumentException("Coordinates are outside the allowed zone"));

        mockMvc.perform(post("/api/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.type").value("DOMAIN_ERROR"))
                .andExpect(jsonPath("$.message").value("Coordinates are outside the allowed zone"));
    }

    @Test
    @DisplayName("Should find nearby issues and return 200 OK")
    void shouldFindNearbyIssues() throws Exception {
        var issue = new UrbanIssue(1L, new IssueTitle("Found it!"), new Coordinates(41.38, 2.17), new UserId(1L));

        when(findNearbyInputPort.execute(any(FindNearbyIssuesQuery.class)))
                .thenReturn(List.of(issue));

        mockMvc.perform(get("/api/issues/nearby")
                        .param("lat", "41.38")
                        .param("lon", "2.17")
                        .param("radius", "500")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Found it!"));
    }

    @Test
    @DisplayName("Should update issue status and return 200 OK")
    void shouldUpdateStatus() throws Exception {
        Long issueId = 1L;
        var request = new UpdateStatusRequest("IN_PROGRESS");

        mockMvc.perform(patch("/api/issues/{id}/status", issueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(updateStatusInputPort).execute(
                argThat(cmd -> cmd.issueId().equals(1L) && cmd.newStatus().equals("IN_PROGRESS"))
        );
    }

    @Test
    @DisplayName("Should return 422 when breaking business rules (Workflow Violation)")
    void shouldReturn422OnWorkflowViolation() throws Exception {
        var request = new UpdateStatusRequest("RESOLVED");

        doThrow(new IllegalStateException("Issue must be IN_PROGRESS before resolving"))
                .when(updateStatusInputPort).execute(any());

        mockMvc.perform(patch("/api/issues/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity()).andExpect(jsonPath("$.type").value("DOMAIN_ERROR"));
    }
}