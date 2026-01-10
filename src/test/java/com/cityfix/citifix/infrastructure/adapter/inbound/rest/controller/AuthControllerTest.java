package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.CreateUserInputPort;
import com.cityfix.citifix.application.port.in.LoginInputPort;
import com.cityfix.citifix.application.port.in.command.CreateUserCommand;
import com.cityfix.citifix.application.port.in.command.LoginCommand;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.auth.LoginRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.auth.RegisterRequest;
import com.cityfix.citifix.infrastructure.config.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginInputPort loginInputPort;

    @MockBean
    private CreateUserInputPort createUserInputPort;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("Login should return 200 and Token")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("alex@cityfix.com", "password123");
        String expectedToken = "jwt-token-example";

        given(loginInputPort.execute(any(LoginCommand.class))).willReturn(expectedToken);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));
    }

    @Test
    @DisplayName("Register should create user, login and return Token")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("new@cityfix.com", "password123", "ROLE_USER");
        User createdUser = User.create("new@cityfix.com", "hash", Set.of("ROLE_USER"));
        String expectedToken = "jwt-token-after-register";

        given(createUserInputPort.execute(any(CreateUserCommand.class))).willReturn(createdUser);
        given(loginInputPort.execute(any(LoginCommand.class))).willReturn(expectedToken);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));
    }

    @Test
    @DisplayName("Login with invalid email format should return 400 Bad Request")
    void shouldReturn400OnInvalidEmail() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email", "pass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}