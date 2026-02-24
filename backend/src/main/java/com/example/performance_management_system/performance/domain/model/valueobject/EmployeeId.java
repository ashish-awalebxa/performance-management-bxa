package com.example.performance_management_system.performance.domain.model.valueobject;

import java.util.Objects;

public record EmployeeId(Long value) {

    public EmployeeId {
        Objects.requireNonNull(value, "Employee id is required");
        if (value <= 0) {
            throw new IllegalArgumentException("Employee id must be positive");
        }
    }
}
