package com.cityfix.citifix.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesTest {

    @Test
    @DisplayName("Should create valid coordinates successfully")
    void shouldCreateValidCoordinates() {
        Double lat = 41.3851;
        Double lon = 2.1734;

        Coordinates coordinates = new Coordinates(lat, lon);

        assertNotNull(coordinates);
        assertEquals(lat, coordinates.latitude());
        assertEquals(lon, coordinates.longitude());
    }

    @Test
    @DisplayName("Should throw exception when values are null")
    void shouldThrowExceptionWhenValuesAreNull() {
        assertThrows(IllegalArgumentException.class, () -> new Coordinates(null, 2.0));
        assertThrows(IllegalArgumentException.class, () -> new Coordinates(41.0, null));
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for invalid latitude (must be between -90 and 90)")
    @ValueSource(doubles = {-90.1, 90.1, -100.0, 100.0})
    void shouldThrowExceptionInvalidLatitude(Double invalidLat) {
        assertThrows(IllegalArgumentException.class, () -> new Coordinates(invalidLat, 2.0));
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for invalid longitude (must be between -180 and 180)")
    @ValueSource(doubles = {-180.1, 180.1, -200.0, 200.0})
    void shouldThrowExceptionInvalidLongitude(Double invalidLon) {
        assertThrows(IllegalArgumentException.class, () -> new Coordinates(41.0, invalidLon));
    }
}