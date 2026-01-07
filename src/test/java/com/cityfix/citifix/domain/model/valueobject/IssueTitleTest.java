package com.cityfix.citifix.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IssueTitleTest {

    @Test
    @DisplayName("Should create a valid title")
    void shouldCreateValidTitle() {
        String validText = "Broken streetlight on Main St";
        IssueTitle title = new IssueTitle(validText);

        assertNotNull(title);
        assertEquals(validText, title.value());
    }

    @Test
    @DisplayName("Should trim whitespace from valid title")
    void shouldTrimWhitespace() {
        IssueTitle title = new IssueTitle("  Dirty Park  ");
        assertEquals("Dirty Park", title.value());
    }

    @Test
    @DisplayName("Should throw exception for null title")
    void shouldThrowExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () -> new IssueTitle(null));
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for empty or blank title")
    @ValueSource(strings = {"", "   ", "\t\n"})
    void shouldThrowExceptionForEmpty(String invalidTitle) {
        assertThrows(IllegalArgumentException.class, () -> new IssueTitle(invalidTitle));
    }

    @Test
    @DisplayName("Should throw exception if title exceeds 100 characters")
    void shouldThrowExceptionForLongTitle() {
        String longTitle = "A".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> new IssueTitle(longTitle));
    }
}