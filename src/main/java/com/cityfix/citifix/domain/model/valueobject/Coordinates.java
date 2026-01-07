package com.cityfix.citifix.domain.model.valueobject;

public record Coordinates(Double latitude, Double longitude) {

    public Coordinates {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude: must be between -90 and 90");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude: must be between -180 and 180");
        }
    }
}