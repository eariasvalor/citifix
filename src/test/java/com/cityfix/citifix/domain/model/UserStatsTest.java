package com.cityfix.citifix.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserStatsTest {

    @Test
    void shouldReturnCorrectRankBasedOnPoints() {
        UserStats hero = new UserStats(1L, 10, 0, 10, 550);
        UserStats neighbor = new UserStats(2L, 5, 0, 2, 70);
        UserStats newbie = new UserStats(3L, 1, 0, 0, 10);

        assertEquals("CITY HERO", hero.getRank());
        assertEquals("COMMITTED NEIGHBOR", neighbor.getRank());
        assertEquals("NEWCOMER", newbie.getRank());
    }
}