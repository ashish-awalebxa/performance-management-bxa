package com.example.performance_management_system.performance.domain.model.valueobject;

import java.util.Objects;

public record ReviewCycleId(Long value) {

    public ReviewCycleId {
        Objects.requireNonNull(value, "Review cycle id is required");
        if (value <= 0) {
            throw new IllegalArgumentException("Review cycle id must be positive");
        }
    }
}
