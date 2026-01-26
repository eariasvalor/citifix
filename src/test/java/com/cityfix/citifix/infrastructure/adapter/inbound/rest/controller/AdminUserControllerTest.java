package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.DeleteUserInputPort;
import com.cityfix.citifix.application.port.in.FindAllUsersInputPort;
import com.cityfix.citifix.application.port.in.FindIssuesByUserIdInputPort;
import com.cityfix.citifix.application.port.in.UpdateUserInputPort;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
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
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private FindIssuesByUserIdInputPort findIssuesByUserIdUseCase;

    @Test
    @DisplayName("GET /api/admin/users - Should return list for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnAllUsers() throws Exception {
        User user = new User(1L, "test@test.com", "hash", Set.of("USER"));

        when(findAllUsersPort.execute(anyInt(), anyInt())).thenReturn(List.of(user));

        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@test.com"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("PATCH /api/admin/users/{id} - Should update and return 200")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateUser() throws Exception {
        Long id = 1L;
        AdminUserController.UpdateUserRequest request = new AdminUserController.UpdateUserRequest("new@email.com", Set.of("ADMIN"));
        User updated = new User(id, "new@email.com", "hash", Set.of("ADMIN"));

        when(updateUserPort.execute(any())).thenReturn(updated);

        mockMvc.perform(patch("/api/admin/users/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").value("ADMIN"));
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} - Should return 204")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/admin/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}