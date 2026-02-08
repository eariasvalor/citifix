package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.DeleteUserInputPort;
import com.cityfix.citifix.application.port.in.FindAllUsersInputPort;
import com.cityfix.citifix.application.port.in.FindIssuesByUserIdInputPort;
import com.cityfix.citifix.application.port.in.UpdateUserInputPort;
import com.cityfix.citifix.application.port.in.command.UpdateUserCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.model.enums.IssueCategory;
import com.cityfix.citifix.domain.model.enums.IssueStatus;
import com.cityfix.citifix.domain.model.valueobject.Coordinates;
import com.cityfix.citifix.domain.model.valueobject.IssueTitle;
import com.cityfix.citifix.domain.model.valueobject.UserId;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.UpdateUserRequest;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FindAllUsersInputPort findAllUsersPort;

    @MockBean
    private UpdateUserInputPort updateUserPort;

    @MockBean
    private DeleteUserInputPort deleteUserPort;

    @MockBean
    private FindIssuesByUserIdInputPort findIssuesByUserIdUseCase;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("GET /api/admin/users - Should return paged users")
    void shouldReturnPagedUsers() throws Exception {
        User user1 = new User(new UserId(1L), "alice@cityfix.com", "pwd", Set.of("ROLE_USER"));
        User user2 = new User(new UserId(2L), "bob@cityfix.com", "pwd", Set.of("ROLE_ADMIN"));

        Page<User> userPage = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 10), 2);

        given(findAllUsersPort.execute(0, 10)).willReturn(userPage);

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[1].email").value("bob@cityfix.com"))
                .andExpect(jsonPath("$.content[0].roles[0]").value("ROLE_USER"));

        verify(findAllUsersPort).execute(0, 10);
    }

    @Test
    @DisplayName("PATCH /api/admin/users/{id} - Should update user")
    void shouldUpdateUser() throws Exception {
        Long userId = 5L;
        UpdateUserRequest request = new UpdateUserRequest("new@cityfix.com", Set.of("ROLE_ADMIN"));
        User updatedUser = new User(new UserId(userId), "new@cityfix.com", "pwd", Set.of("ROLE_ADMIN"));

        given(updateUserPort.execute(any(UpdateUserCommand.class))).willReturn(updatedUser);

        mockMvc.perform(patch("/api/admin/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("new@cityfix.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"));

        verify(updateUserPort).execute(argThat(cmd ->
                cmd.userId().equals(userId)
                        && cmd.email().equals("new@cityfix.com")
                        && cmd.roles().contains("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} - Should return 204")
    void shouldDeleteUser() throws Exception {
        Long userId = 3L;
        doNothing().when(deleteUserPort).execute(userId);

        mockMvc.perform(delete("/api/admin/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(deleteUserPort).execute(userId);
    }

    @Test
    @DisplayName("GET /api/admin/users/{id}/issues - Should return paged issues")
    void shouldReturnUserIssues() throws Exception {
        Long userId = 7L;
        UrbanIssue issue = UrbanIssue.rehydrate(
                10L,
                new IssueTitle("Pothole"),
                "Deep pothole",
                new Coordinates(41.0, 2.0),
                new UserId(userId),
                IssueStatus.REPORTED,
                IssueCategory.ROAD,
                null
        );

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UrbanIssue> issuesPage = new PageImpl<>(List.of(issue), pageable, 1);

        given(findIssuesByUserIdUseCase.execute(eq(userId), eq("REPORTED"), eq("ROAD"), any(Pageable.class)))
                .willReturn(issuesPage);

        mockMvc.perform(get("/api/admin/users/{id}/issues", userId)
                        .param("status", "REPORTED")
                        .param("category", "ROAD")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].status").value("REPORTED"))
                .andExpect(jsonPath("$.content[0].category").value("ROAD"));

        verify(findIssuesByUserIdUseCase).execute(eq(userId), eq("REPORTED"), eq("ROAD"),
                argThat(p -> p.getPageNumber() == 0
                        && p.getPageSize() == 5
                        && Sort.Direction.DESC.equals(p.getSort().getOrderFor("createdAt").getDirection())));
    }
}
