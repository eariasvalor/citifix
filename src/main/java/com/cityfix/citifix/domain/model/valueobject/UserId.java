package com.cityfix.citifix.domain.model.valueobject;

public record UserId(Long value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }

    public Long getValue() {
        return value;
    }
}