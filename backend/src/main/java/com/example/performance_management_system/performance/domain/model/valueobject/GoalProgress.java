package com.example.performance_management_system.performance.domain.model.valueobject;

import java.util.List;
import java.util.Objects;

public record GoalProgress(GoalStatusSnapshot status, List<KeyResultProgress> keyResults) {

    public GoalProgress {
        Objects.requireNonNull(status, "Goal status is required");
        Objects.requireNonNull(keyResults, "Key results are required");
    }

    public double averageProgressRatio() {
        return keyResults.stream()
                .mapToDouble(KeyResultProgress::ratio)
                .average()
                .orElse(0.0);
    }
}
