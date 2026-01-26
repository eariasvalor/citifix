package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.DeleteUserInputPort;
import com.cityfix.citifix.application.port.in.FindAllUsersInputPort;
import com.cityfix.citifix.application.port.in.FindIssuesByUserIdInputPort;
import com.cityfix.citifix.application.port.in.UpdateUserInputPort;
import com.cityfix.citifix.application.port.in.command.UpdateUserCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response.IssueResponse;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Endpoints for user administration (Admin only)")
public class AdminUserController {

    private final FindAllUsersInputPort findAllUsersPort;
    private final UpdateUserInputPort updateUserPort;
    private final DeleteUserInputPort deleteUserPort;
    private final FindIssuesByUserIdInputPort findIssuesByUserIdUseCase;

    @GetMapping
    @Operation(summary = "List all users with pagination")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        var users = findAllUsersPort.execute(page, size);
        return ResponseEntity.ok(users.stream()
                .map(UserResponse::fromDomain)
                .toList());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a user (email or roles)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        var command = new UpdateUserCommand(id, request.email(), request.roles());
        var updatedUser = updateUserPort.execute(command);

        return ResponseEntity.ok(UserResponse.fromDomain(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        deleteUserPort.execute(id);
        return ResponseEntity.noContent().build();
    }

    public record UpdateUserRequest(String email, Set<String> roles) {
    }

    @GetMapping("/{id}/issues")
    @Operation(summary = "Get issues by user with filters and pagination")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<IssueResponse>> getUserIssues(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<UrbanIssue> issuesPage = findIssuesByUserIdUseCase.execute(id, status, category, pageable);

        Page<IssueResponse> responsePage = issuesPage.map(IssueResponse::fromDomain);

        return ResponseEntity.ok(responsePage);
    }
}