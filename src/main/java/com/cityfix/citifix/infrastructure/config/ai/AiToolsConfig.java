package com.cityfix.citifix.infrastructure.config.ai;

import com.cityfix.citifix.application.port.in.CreateIssueInputPort;
import com.cityfix.citifix.application.port.in.command.CreateIssueCommand;
import com.cityfix.citifix.domain.model.UrbanIssue;
import com.cityfix.citifix.domain.port.out.IssueRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
public class AiToolsConfig {

    @Bean
    @Description("Query urban incidents reported near a location")
    public Function<NearbyQueryRequest, List<UrbanIssue>> fetchNearbyIssues(IssueRepositoryPort repository) {
        return request -> repository.findNearby(
                request.lat(), request.lon(), 5000.0, null, null, 0, 10
        );
    }

    @Bean
    @Description("Create a new urban issue")
    public Function<CreateIssueRequest, String> createUrbanIssue(CreateIssueInputPort createPort) {
        return request -> {
            var command = new CreateIssueCommand(
                    request.title(), request.description(), request.lat(), request.lon(), request.category(), request.email()
            );
            UrbanIssue issue = createPort.execute(command, null);
            return "Issue created successfully. ID: " + issue.getId();
        };
    }

    public record NearbyQueryRequest(Double lat, Double lon) {}
    public record CreateIssueRequest(String title, String description, Double lat, Double lon, String category, String email) {}
}