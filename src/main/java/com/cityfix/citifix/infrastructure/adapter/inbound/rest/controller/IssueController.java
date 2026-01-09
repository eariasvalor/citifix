package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.CreateIssueInputPort;
import com.cityfix.citifix.application.port.in.FindNearbyIssuesInputPort;
import com.cityfix.citifix.application.port.in.UpdateIssueStatusInputPort;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.application.port.in.command.UpdateIssueStatusCommand;
import com.cityfix.citifix.application.port.in.query.FindNearbyIssuesQuery;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.CreateIssueRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.UpdateStatusRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response.IssueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
@Tag(name = "Urban Issues", description = "Operations related to reporting and managing city issues")
public class IssueController {

    private final CreateIssueInputPort createIssueInputPort;
    private final FindNearbyIssuesInputPort findNearbyIssuesInputPort;
    private final UpdateIssueStatusInputPort updateIssueStatusInputPort;

    @Operation(summary = "Report a new issue", description = "Creates a new urban issue. Latitude must be between -90 and 90.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Issue created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<IssueResponse> create(@RequestBody @Valid CreateIssueRequest request) {

        Long fakeUserId = 1L;

        var command = new CreateIssueCommand(
                request.title(),
                request.latitude(),
                request.longitude(),
                fakeUserId
        );

        UrbanIssue domainIssue = createIssueInputPort.execute(command);

        var response = new IssueResponse(
                domainIssue.getId(),
                domainIssue.getTitle().value(),
                domainIssue.getStatus().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Find nearby issues", description = "Returns a paginated list of issues within a specific radius (in meters).")
    @GetMapping("/nearby")
    public ResponseEntity<List<IssueResponse>> findNearby(
            @Parameter(description = "Center Latitude", example = "41.3879") @RequestParam("lat") Double lat,
            @Parameter(description = "Center Longitude", example = "2.1699") @RequestParam("lon") Double lon,
            @Parameter(description = "Radius in meters", example = "500") @RequestParam("radius") Double radius,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        var query = new FindNearbyIssuesQuery(lat, lon, radius, page, size);

        List<UrbanIssue> issues = findNearbyIssuesInputPort.execute(query);

        List<IssueResponse> response = issues.stream()
                .map(issue -> new IssueResponse(
                        issue.getId(),
                        issue.getTitle().value(),
                        issue.getStatus().name()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update issue status", description = "Updates the status (e.g., from REPORTED to IN_PROGRESS). Enforces business workflow.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "422", description = "Business rule violation (e.g. invalid transition)")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStatusRequest request
    ) {
        var command = new UpdateIssueStatusCommand(id, request.status());

        updateIssueStatusInputPort.execute(command);

        return ResponseEntity.ok().build();
    }
}