package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.port.in.CreateIssueInputPort;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.request.CreateIssueRequest;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response.IssueResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {

    private final CreateIssueInputPort createIssueInputPort;

    @PostMapping
    public ResponseEntity<IssueResponse> create(@RequestBody @Valid CreateIssueRequest request) {

        //TODO: when JWT is set up, obtain userID from SecurityContext
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
}