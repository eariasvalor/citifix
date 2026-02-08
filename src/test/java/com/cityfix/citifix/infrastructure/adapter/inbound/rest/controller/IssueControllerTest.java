package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.*;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;
import com.cityfix.citifix.application.port.in.query.FindNearbyIssuesQuery;
import com.cityfix.citifix.application.usecase.issue.DeleteIssueUseCase;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.CreateIssueRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.UpdateIssueRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.UpdateStatusRequest;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
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
    private UpdateIssueInputPort updateIssueInputPort;
    @MockBean
    private CreateUserInputPort createUserInputPort;
    @MockBean
    private LoginInputPort loginInputPort;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private DeleteIssueUseCase deleteIssueInputPort;
    @MockBean
    private FindIssueByIdInputPort findIssueByIdInputPort;
    @MockBean
    private FindIssuesByUserIdInputPort findIssuesByUserIdInputPort;
    @MockBean
    private FindAllIssuesInputPort findAllIssuesInputPort;

    @Test
    @DisplayName("POST /api/issues - Should report issue and return 201")
    void shouldCreateIssueSuccessfully() throws Exception {
        CreateIssueRequest request = new CreateIssueRequest("Pothole", "desc", 41.5, 2.0, "ROAD");
        MockMultipartFile dataPart = new MockMultipartFile("data", "", "application/json", objectMapper.writeValueAsBytes(request));


        UrbanIssue mockIssue = UrbanIssue.rehydrate(
                100L,
                new IssueTitle("Pothole"),
                "desc",
                new Coordinates(41.5, 2.0),
                new UserId(1L),
                IssueStatus.REPORTED,
                IssueCategory.ROAD,
                null
        );

        Principal mockPrincipal = mock(Principal.class);
        given(mockPrincipal.getName()).willReturn("user");
        given(createIssueInputPort.execute(any(CreateIssueCommand.class), any())).willReturn(mockIssue);

        mockMvc.perform(multipart("/api/issues")
                        .file(dataPart)
                        .principal(mockPrincipal)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("Pothole"));
    }

    @Test
    @DisplayName("GET /api/issues/nearby - Should return list of issues")
    void shouldFindNearbyIssues() throws Exception {
        UrbanIssue issue1 = UrbanIssue.rehydrate(1L, new IssueTitle("I1"), "d", new Coordinates(41.0, 2.0), new UserId(1L), IssueStatus.REPORTED, IssueCategory.OTHER, null);
        UrbanIssue issue2 = UrbanIssue.rehydrate(2L, new IssueTitle("I2"), "d", new Coordinates(41.01, 2.01), new UserId(2L), IssueStatus.IN_PROGRESS, IssueCategory.OTHER, null);

        when(findNearbyIssuesInputPort.execute(any(FindNearbyIssuesQuery.class))).thenReturn(List.of(issue1, issue2));

        mockMvc.perform(get("/api/issues/nearby")
                        .param("lat", "41.0")
                        .param("lon", "2.0")
                        .param("radius", "500")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("I1"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("PATCH /api/issues/{id}/status - Should update status successfully")
    void shouldUpdateIssueStatus() throws Exception {
        Long issueId = 1L;
        UpdateStatusRequest request = new UpdateStatusRequest("IN_PROGRESS");

        UrbanIssue updatedIssue = UrbanIssue.rehydrate(
                issueId,
                new IssueTitle("Test Issue"),
                "Desc",
                new Coordinates(41.0, 2.0),
                new UserId(1L),
                IssueStatus.IN_PROGRESS,
                IssueCategory.OTHER,
                null
        );

        given(updateIssueStatusInputPort.execute(any(UpdateIssueStatusCommand.class))).willReturn(updatedIssue);

        mockMvc.perform(patch("/api/issues/{id}/status", issueId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("PATCH /api/issues/{id} - Should update issue details fully")
    void shouldUpdateIssueDetails() throws Exception {
        String newTitleStr = "New Title";
        String userEmail = "alex@cityfix.com";
        UpdateIssueRequest request = new UpdateIssueRequest(newTitleStr, "Desc", "IN_PROGRESS", "ROAD", false);

        UrbanIssue mockIssue = new UrbanIssue(
                1L,
                new IssueTitle(newTitleStr),
                "Desc",
                new Coordinates(0.0, 0.0),
                new UserId(1L),
                IssueStatus.IN_PROGRESS,
                IssueCategory.ROAD,
                null,
                LocalDateTime.now()
        );

        given(updateIssueInputPort.execute(any(UpdateIssueCommand.class), eq(userEmail)))
                .willReturn(mockIssue);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/issues/{id}", 1L)
                        .file(dataPart)
                        .principal(() -> userEmail)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(newTitleStr));

        verify(updateIssueInputPort).execute(any(UpdateIssueCommand.class), eq(userEmail));
    }

    @Test
    @DisplayName("GET /api/issues/nearby - Should filter by status and category")
    void shouldFindNearbyIssuesWithFilters() throws Exception {
        UrbanIssue issue = UrbanIssue.rehydrate(1L, new IssueTitle("Issue"), "Desc", new Coordinates(41.0, 2.0), new UserId(1L), IssueStatus.REPORTED, IssueCategory.ROAD, null);

        when(findNearbyIssuesInputPort.execute(any(FindNearbyIssuesQuery.class))).thenReturn(List.of(issue));

        mockMvc.perform(get("/api/issues/nearby")
                        .param("lat", "41.0")
                        .param("lon", "2.0")
                        .param("status", "REPORTED")
                        .param("category", "ROAD"))
                .andExpect(status().isOk());

        verify(findNearbyIssuesInputPort).execute(argThat(query ->
                query.status().equals("REPORTED") && query.category().equals("ROAD")
        ));
    }

    @Test
    @DisplayName("DELETE /api/issues/{id} - Should return 204 No Content")
    void shouldDeleteIssue() throws Exception {
        Long issueId = 1L;
        String adminEmail = "admin@cityfix.com";
        Principal mockPrincipal = mock(Principal.class);
        given(mockPrincipal.getName()).willReturn(adminEmail);

        doNothing().when(deleteIssueInputPort).execute(issueId, adminEmail);

        mockMvc.perform(delete("/api/issues/{id}", issueId)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Issue deleted successfully"));;

        verify(deleteIssueInputPort).execute(issueId, adminEmail);
    }

    private UrbanIssue createMockIssue(Long id, IssueStatus status) {
        return new UrbanIssue(
                id,
                new IssueTitle("Test Title"),
                "Description",
                new Coordinates(0.0, 0.0),
                new UserId(1L),
                status,
                IssueCategory.OTHER,
                null,
                LocalDateTime.now()
        );
    }
    @Test
    @DisplayName("Outside-In: Should return 400 when creating issue with invalid coordinates")
    void shouldRejectInvalidCoordinates() throws Exception {
        CreateIssueRequest invalidRequest = new CreateIssueRequest("Title", "Desc", 150.0, 2.0, "ROAD");
        MockMultipartFile dataPart = new MockMultipartFile("data", "", "application/json",
                objectMapper.writeValueAsBytes(invalidRequest));

        mockMvc.perform(multipart("/api/issues")
                        .file(dataPart)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/issues/{id} - Should allow USER to delete their own issue")
    void shouldAllowUserToDeleteOwnIssue() throws Exception {
        Long issueId = 1L;
        String userEmail = "alex@cityfix.com";
        Principal mockPrincipal = mock(Principal.class);

        given(mockPrincipal.getName()).willReturn(userEmail);

        doNothing().when(deleteIssueInputPort).execute(issueId, userEmail);

        mockMvc.perform(delete("/api/issues/{id}", issueId)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Issue deleted successfully"));

        verify(deleteIssueInputPort).execute(issueId, userEmail);
    }

    @Test
    @DisplayName("PATCH /api/issues/{id} - USER can change title but NOT status")
    void userCannotChangeStatus() throws Exception {
        Long issueId = 1L;
        String userEmail = "citizen@cityfix.com";

        UpdateIssueRequest request = new UpdateIssueRequest("New Title", "Desc", "RESOLVED", "ROAD", false);

        UrbanIssue resultIssue = createMockIssue(issueId, IssueStatus.REPORTED);

        Principal mockPrincipal = mock(Principal.class);
        given(mockPrincipal.getName()).willReturn(userEmail);

        given(updateIssueInputPort.execute(any(UpdateIssueCommand.class), eq(userEmail)))
                .willReturn(resultIssue);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "", "application/json", objectMapper.writeValueAsBytes(request));

        mockMvc.perform(multipart("/api/issues/{id}", issueId)
                        .file(dataPart)
                        .principal(mockPrincipal)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REPORTED"));

        verify(updateIssueInputPort).execute(any(UpdateIssueCommand.class), eq(userEmail));
    }

    @Test
    @DisplayName("GET /api/issues/{id} - Must return 200 and the issue")
    void shouldReturnIssueFound() throws Exception {
        Long id = 1L;
        UrbanIssue issue = UrbanIssue.rehydrate(
                id,
                new IssueTitle("Broken streetlight"),
                "No light",
                new Coordinates(41.0, 2.0),
                new UserId(1L),
                IssueStatus.REPORTED,
                IssueCategory.LIGHTING,
                null
        );

        given(findIssueByIdInputPort.execute(id)).willReturn(Optional.of(issue));

        mockMvc.perform(get("/api/issues/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Broken streetlight"))
                .andExpect(jsonPath("$.category").value("LIGHTING"));
    }

    @Test
    @DisplayName("GET /api/issues/{id} - Must return 404 if it doesn't exist")
    void shouldReturn404WhenNotFound() throws Exception {
        Long id = 99L;
        given(findIssueByIdInputPort.execute(id)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/issues/{id}", id))
                .andExpect(status().isNotFound());
    }
}