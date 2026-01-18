package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.*;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;
import com.cityfix.citifix.application.port.in.query.FindNearbyIssuesQuery;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.issue.CreateIssueRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.UpdateStatusRequest;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IssueController.class)
@AutoConfigureMockMvc(addFilters = false)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateIssueInputPort createIssueInputPort;

    @MockBean
    private FindNearbyIssuesInputPort findNearbyIssuesInputPort;

    @MockBean
    private UpdateIssueStatusInputPort updateIssueStatusInputPort;

    @MockBean
    private CreateUserInputPort createUserInputPort;

    @MockBean
    private LoginInputPort loginInputPort;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Mock
    private IssueRepositoryPort issueRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Test
    @DisplayName("POST /api/issues - Should report issue and return 201 Created with full response")
    void shouldCreateIssueSuccessfully() throws Exception {
        CreateIssueRequest request = new CreateIssueRequest(
                "Pothole",
                "Dangerous hole in the road",
                41.5,
                2.0,
                "ROAD"
        );

        UrbanIssue mockIssue = UrbanIssue.rehydrate(
                100L,
                new IssueTitle("Pothole"),
                new Coordinates(41.5, 2.0),
                new UserId(1L),
                IssueStatus.REPORTED,
                IssueCategory.ROAD
        );

        given(createIssueInputPort.execute(any(CreateIssueCommand.class))).willReturn(mockIssue);

        mockMvc.perform(post("/api/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "citizen@test.com")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("Pothole"))
                .andExpect(jsonPath("$.category").value("ROAD"))
                .andExpect(jsonPath("$.status").value("REPORTED"));
    }

    @Test
    @DisplayName("GET /api/issues/nearby - Should return list of issues")
    void shouldFindNearbyIssues() throws Exception {
        UrbanIssue issue1 = UrbanIssue.rehydrate(
                1L, new IssueTitle("Issue 1"), new Coordinates(41.0, 2.0), new UserId(1L), IssueStatus.REPORTED, IssueCategory.OTHER);
        UrbanIssue issue2 = UrbanIssue.rehydrate(
                2L, new IssueTitle("Issue 2"), new Coordinates(41.01, 2.01), new UserId(2L), IssueStatus.IN_PROGRESS, IssueCategory.OTHER);

        when(findNearbyIssuesInputPort.execute(any(FindNearbyIssuesQuery.class)))
                .thenReturn(List.of(issue1, issue2));

        mockMvc.perform(get("/api/issues/nearby")
                        .param("lat", "41.0")
                        .param("lon", "2.0")
                        .param("radius", "500")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(() -> "user@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Issue 1"))
                .andExpect(jsonPath("$[0].status").value("REPORTED"))
                .andExpect(jsonPath("$[1].title").value("Issue 2"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("PATCH /api/issues/{id}/status - Should update status successfully")
    void shouldUpdateIssueStatus() throws Exception {
        Long issueId = 1L;
        UpdateStatusRequest request = new UpdateStatusRequest("IN_PROGRESS");

        mockMvc.perform(patch("/api/issues/{id}/status", issueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .principal(() -> "admin@test.com"))
                .andExpect(status().isOk());

        verify(updateIssueStatusInputPort).execute(any(UpdateIssueStatusCommand.class));
    }
}