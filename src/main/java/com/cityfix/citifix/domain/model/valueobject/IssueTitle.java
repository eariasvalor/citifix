package com.cityfix.citifix.domain.model.valueobject;

public record IssueTitle(String value) {

    public IssueTitle {
        if (value == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (value.length() > 100) {
            throw new IllegalArgumentException("Title length cannot exceed 100 characters");
        }
    }
}