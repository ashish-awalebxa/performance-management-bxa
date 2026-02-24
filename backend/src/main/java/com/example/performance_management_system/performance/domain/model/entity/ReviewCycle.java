package com.example.performance_management_system.performance.domain.model.entity;

import com.example.performance_management_system.performance.domain.model.valueobject.ReviewCycleId;

import java.time.LocalDate;
import java.util.Objects;

public final class ReviewCycle {
    private final ReviewCycleId cycleId;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public ReviewCycle(ReviewCycleId cycleId, LocalDate startDate, LocalDate endDate) {
        this.cycleId = Objects.requireNonNull(cycleId, "Review cycle id is required");
        this.startDate = Objects.requireNonNull(startDate, "Start date is required");
        this.endDate = Objects.requireNonNull(endDate, "End date is required");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Review cycle end date cannot be before start date");
        }
    }

    public ReviewCycleId cycleId() {
        return cycleId;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }
}
