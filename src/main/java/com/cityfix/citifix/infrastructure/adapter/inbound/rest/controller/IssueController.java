package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.*;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.application.port.in.command.UpdateIssueCommand;
import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;
import com.cityfix.citifix.application.port.in.query.FindNearbyIssuesQuery;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.issue.CreateIssueRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.issue.UpdateIssueRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.UpdateStatusRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response.IssueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final CreateIssueInputPort createIssueInputPort;
    private final FindNearbyIssuesInputPort findNearbyIssuesInputPort;
    private final UpdateIssueStatusInputPort updateIssueStatusInputPort;
    private final UpdateIssueInputPort updateIssueInputPort;
    private final DeleteIssueInputPort deleteIssueInputPort;
    private final FindIssueByIdInputPort findIssueByIdInputPort;
    private final FindIssuesByUserIdInputPort findIssuesByUserIdUseCase;

    @Operation(summary = "Create a new issue", description = "Report an issue with an optional image.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IssueResponse> createIssue(
            @RequestPart("data") @Valid CreateIssueRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) {
        var command = new CreateIssueCommand(
                request.title(),
                request.description(),
                request.latitude(),
                request.longitude(),
                request.category(),
                principal.getName()
        );
        UrbanIssue issue = createIssueInputPort.execute(command, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(IssueResponse.fromDomain(issue));
    }

    @Operation(summary = "Find nearby issues", description = "Returns a paginated list of issues within a specific radius (in meters) with optional filters.")
    @GetMapping("/nearby")
    public ResponseEntity<List<IssueResponse>> findNearby(
            @Parameter(description = "Center Latitude", example = "41.3879") @RequestParam("lat") Double lat,
            @Parameter(description = "Center Longitude", example = "2.1699") @RequestParam("lon") Double lon,
            @Parameter(description = "Radius in meters", example = "5000") @RequestParam(value = "radius", defaultValue = "5000") Double radius,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by category") @RequestParam(required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        var query = new FindNearbyIssuesQuery(lat, lon, status, category, radius, page, size);

        List<UrbanIssue> issues = findNearbyIssuesInputPort.execute(query);

        if (issues == null) {
            return ResponseEntity.ok(List.of());
        }

        var response = issues.stream()
                .map(IssueResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update issue status",
            description = "Updates the issue status to any valid state defined in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value provided")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<IssueResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStatusRequest request
    ) {
        var command = new UpdateIssueStatusCommand(id, request.status());

        UrbanIssue updatedIssue = updateIssueStatusInputPort.execute(command);

        return ResponseEntity.ok(IssueResponse.fromDomain(updatedIssue));
    }


    @Operation(summary = "Update issue details", description = "Updates title, description, category or status fully.")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IssueResponse> updateIssue(
            @PathVariable Long id,
            @RequestPart("data") UpdateIssueRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) throws IOException {
        var command = new UpdateIssueCommand(
                id,
                request.title(),
                request.description(),
                request.status(),
                request.category(),
                image
        );

        UrbanIssue updatedIssue = updateIssueInputPort.execute(command, principal.getName());

        return ResponseEntity.ok(IssueResponse.fromDomain(updatedIssue));
    }

    @Operation(summary = "Delete an issue", description = "Deletes an issue. Requires ADMIN role.")
    @ApiResponse(responseCode = "204", description = "Deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @DeleteMapping("/{id}")
    public ResponseEntity<IssueResponse.MessageResponse> deleteIssue(@PathVariable Long id, Principal principal) {
        deleteIssueInputPort.execute(id, principal.getName());
        return ResponseEntity.ok(new IssueResponse.MessageResponse("Issue deleted successfully"));
    }

    @Operation(summary = "Get issue by ID", description = "Retrieves a single urban issue by its unique identifier.")
    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long id) {
        return findIssueByIdInputPort.execute(id)
                .map(issue -> ResponseEntity.ok(IssueResponse.fromDomain(issue)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get issues by user with filters and pagination")
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