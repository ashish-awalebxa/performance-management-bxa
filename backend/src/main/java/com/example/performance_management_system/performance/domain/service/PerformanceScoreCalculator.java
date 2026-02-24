package com.example.performance_management_system.performance.domain.service;

import com.example.performance_management_system.performance.domain.model.valueobject.GoalProgress;
import com.example.performance_management_system.performance.domain.model.valueobject.GoalStatusSnapshot;
import com.example.performance_management_system.performance.domain.model.valueobject.PerformanceScore;
import com.example.performance_management_system.performance.domain.model.valueobject.RatingCriteria;

import java.util.List;
import java.util.Objects;

public final class PerformanceScoreCalculator {

    public PerformanceScore calculate(List<GoalProgress> goals, RatingCriteria ratingCriteria) {
        Objects.requireNonNull(goals, "Goals are required");
        Objects.requireNonNull(ratingCriteria, "Rating criteria are required");

        if (goals.isEmpty()) {
            return new PerformanceScore(1.0);
        }

        double averageScore = goals.stream()
                .mapToDouble(goal -> goal.averageProgressRatio() * statusWeight(goal.status(), ratingCriteria) * 5.0)
                .average()
                .orElse(1.0);

        return PerformanceScore.fromRaw(averageScore);
    }

    private double statusWeight(GoalStatusSnapshot status, RatingCriteria ratingCriteria) {
        return switch (status) {
            case COMPLETED -> ratingCriteria.completedWeight();
            case APPROVED -> ratingCriteria.approvedWeight();
            case SUBMITTED -> ratingCriteria.submittedWeight();
            case DRAFT -> ratingCriteria.draftWeight();
            case REJECTED -> ratingCriteria.rejectedWeight();
            case ARCHIVED -> ratingCriteria.archivedWeight();
        };
    }
}
