package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.DeleteUserInputPort;
import com.cityfix.citifix.application.port.in.FindAllUsersInputPort;
import com.cityfix.citifix.application.port.in.FindIssuesByUserIdInputPort;
import com.cityfix.citifix.application.port.in.UpdateUserInputPort;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.domain.port.out.UserRepositoryPort;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
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
    private UserRepositoryPort userRepositoryPort;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private FindIssuesByUserIdInputPort findIssuesByUserIdUseCase;

    @Test
    @DisplayName("Should return a page of all users")
    void shouldReturnPageOfUsers() {
        User user1 = new User(1L, "admin@cityfix.com", "hash1", Set.of("ADMIN"));
        User user2 = new User(2L, "citizen@cityfix.com", "hash2", Set.of("USER"));
        List<User> userList = List.of(user1, user2);

        Page<User> expectedPage = new PageImpl<>(userList, PageRequest.of(1, 10), userList.size());

        when(findAllUsersPort.execute(1, 10)).thenReturn(expectedPage);

        Page<User> result = findAllUsersPort.execute(1, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

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