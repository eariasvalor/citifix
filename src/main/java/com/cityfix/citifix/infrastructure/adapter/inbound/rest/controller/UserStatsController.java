package com.cityfix.citifix.infrastructure.adapter.inbound.rest.controller;

import com.cityfix.citifix.application.usecase.user.GetUserStatsUseCase;
import com.cityfix.citifix.domain.model.UserStats;
import com.cityfix.citifix.infrastructure.adapter.inbound.rest.dto.response.UserStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserStatsController {
    private final GetUserStatsUseCase getUserStatsUseCase;

    @GetMapping("/{id}/stats")
    public ResponseEntity<UserStatsResponse> getStats(@PathVariable Long id) {
        UserStats stats = getUserStatsUseCase.execute(id);
        return ResponseEntity.ok(UserStatsResponse.fromDomain(stats));
    }
}