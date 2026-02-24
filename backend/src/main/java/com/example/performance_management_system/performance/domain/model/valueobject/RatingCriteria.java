package com.example.performance_management_system.performance.domain.model.valueobject;

public record RatingCriteria(double completedWeight,
                             double approvedWeight,
                             double submittedWeight,
                             double draftWeight,
                             double rejectedWeight,
                             double archivedWeight) {

    public static RatingCriteria standard() {
        return new RatingCriteria(1.0, 0.9, 0.75, 0.5, 0.2, 0.0);
    }

    public RatingCriteria {
        validateWeight(completedWeight);
        validateWeight(approvedWeight);
        validateWeight(submittedWeight);
        validateWeight(draftWeight);
        validateWeight(rejectedWeight);
        validateWeight(archivedWeight);
    }

    private static void validateWeight(double weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("Each rating weight must be between 0.0 and 1.0");
        }
    }
}
