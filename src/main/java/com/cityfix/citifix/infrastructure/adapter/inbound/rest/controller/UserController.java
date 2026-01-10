package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.query.GetUserProfileInputPort;
import com.cityfix.citifix.domain.model.User;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management and profile operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final GetUserProfileInputPort getUserProfileInputPort;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieves the profile information of the authenticated user based on the JWT token.")
    public ResponseEntity<UserResponse> getMyProfile(Principal principal) {
        String userEmail = principal.getName();

        User user = getUserProfileInputPort.execute(userEmail);

        return ResponseEntity.ok(UserResponse.fromDomain(user));
    }
}