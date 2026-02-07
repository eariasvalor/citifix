package com.cityfix.citifix.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Map;

@Getter
@AllArgsConstructor
public class GlobalStats {
    private final long totalIssues;
    private final Map<String, Long> issuesByStatus;
    private final Map<String, Long> issuesByCategory;


    public double getResolutionRate() {
        if (totalIssues == 0) return 0.0;
        long resolved = issuesByStatus.getOrDefault("RESOLVED", 0L);
        return (double) resolved / totalIssues * 100;
    }


    public String getCityHealthStatus() {
        long openIssues = issuesByStatus.getOrDefault("REPORTED", 0L) +
                issuesByStatus.getOrDefault("IN_PROGRESS", 0L);

        if (openIssues < 10) return "EXCELLENT";
        if (openIssues < 50) return "STABLE";
        return "CRITICAL_ATTENTION_REQUIRED";
    }
}