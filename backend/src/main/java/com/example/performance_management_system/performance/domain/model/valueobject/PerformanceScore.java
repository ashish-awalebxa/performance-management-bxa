package com.example.performance_management_system.performance.domain.model.valueobject;

public record PerformanceScore(double value) {

    public PerformanceScore {
        if (value < 1.0 || value > 5.0) {
            throw new IllegalArgumentException("Performance score must be between 1.0 and 5.0");
        }
    }

    public static PerformanceScore fromRaw(double rawValue) {
        double boundedValue = Math.max(1.0, Math.min(5.0, rawValue));
        double roundedValue = Math.round(boundedValue * 100.0) / 100.0;
        return new PerformanceScore(roundedValue);
    }
}
