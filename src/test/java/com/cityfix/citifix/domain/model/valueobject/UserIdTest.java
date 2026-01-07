package com.cityfix.citifix.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    @DisplayName("Should create valid user ID")
    void shouldCreateValidUserId() {
        UserId userId = new UserId(100L);
        assertNotNull(userId);
        assertEquals(100L, userId.value());
    }

    @Test
    @DisplayName("Should throw exception for null ID")
    void shouldThrowExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(null));
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for non-positive ID (0 or negative)")
    @ValueSource(longs = {0L, -1L, -500L})
    void shouldThrowExceptionForNonPositiveId(Long invalidId) {
        assertThrows(IllegalArgumentException.class, () -> new UserId(invalidId));
    }
}