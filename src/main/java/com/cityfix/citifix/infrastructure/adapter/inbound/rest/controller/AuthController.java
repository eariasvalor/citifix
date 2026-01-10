package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.CreateUserInputPort;
import com.cityfix.citifix.application.port.in.LoginInputPort;
import com.cityfix.citifix.application.port.in.command.CreateUserCommand;
import com.cityfix.citifix.application.port.in.command.LoginCommand;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.auth.AuthResponse;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.auth.LoginRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final CreateUserInputPort createUserUseCase;
    private final LoginInputPort loginUseCase;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request) {
        var command = new CreateUserCommand(request.email(), request.password(), request.role());
        createUserUseCase.execute(command);

        var loginCommand = new LoginCommand(request.email(), request.password());
        String token = loginUseCase.execute(loginCommand);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT Token")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        var command = new LoginCommand(request.email(), request.password());
        String token = loginUseCase.execute(command);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}