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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final CreateIssueInputPort createIssueInputPort;
    private final FindNearbyIssuesInputPort findNearbyIssuesInputPort;
    private final UpdateIssueStatusInputPort updateIssueStatusInputPort;

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

    @GetMapping("/nearby")
    public ResponseEntity<List<IssueResponse>> findNearby(
            @RequestParam("lat") Double lat,
            @RequestParam("lon") Double lon,
            @RequestParam("radius") Double radius,
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