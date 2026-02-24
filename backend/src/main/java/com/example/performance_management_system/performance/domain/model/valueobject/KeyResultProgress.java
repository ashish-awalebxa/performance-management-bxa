package com.example.performance_management_system.performance.domain.model.valueobject;

public record KeyResultProgress(double currentValue, double targetValue) {

    public KeyResultProgress {
        if (targetValue < 0) {
            throw new IllegalArgumentException("Target value must be zero or positive");
        }
    }

    public double ratio() {
        if (targetValue <= 0.0) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, currentValue / targetValue));
    }
}
