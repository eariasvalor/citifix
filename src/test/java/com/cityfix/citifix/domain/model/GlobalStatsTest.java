package com.cityfix.citifix.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class GlobalStatsTest {

    @Test
    @DisplayName("Should correctly calculate the resolution rate")
    void shouldCalculateResolutionRate() {
        Map<String, Long> statusMap = Map.of(
                "REPORTED", 5L,
                "IN_PROGRESS", 5L,
                "RESOLVED", 10L
        );

        GlobalStats stats = new GlobalStats(20L, statusMap, Map.of());

        assertThat(stats.getResolutionRate()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Should return 0 if there are no incidences")
    void shouldReturnZeroIfNoIssues() {
        GlobalStats stats = new GlobalStats(0L, Map.of(), Map.of());
        assertThat(stats.getResolutionRate()).isEqualTo(0.0);
    }
}