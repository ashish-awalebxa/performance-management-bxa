package com.example.performance_management_system.performance.domain.model.entity;

import com.example.performance_management_system.performance.domain.model.valueobject.EmployeeId;
import com.example.performance_management_system.performance.domain.model.valueobject.PerformanceScore;
import com.example.performance_management_system.performance.domain.model.valueobject.ReviewCycleId;

import java.util.Objects;

public final class PerformanceReview {
    private final Long reviewId;
    private final EmployeeId employeeId;
    private final ReviewCycleId reviewCycleId;
    private PerformanceScore score;
    private PerformanceReviewStatus status;
    private String managerJustification;
    private String hrJustification;

    public PerformanceReview(
            Long reviewId,
            EmployeeId employeeId,
            ReviewCycleId reviewCycleId,
            PerformanceScore score,
            PerformanceReviewStatus status,
            String managerJustification,
            String hrJustification
    ) {
        this.reviewId = Objects.requireNonNull(reviewId, "Review id is required");
        this.employeeId = Objects.requireNonNull(employeeId, "Employee id is required");
        this.reviewCycleId = Objects.requireNonNull(reviewCycleId, "Review cycle id is required");
        this.score = Objects.requireNonNull(score, "Performance score is required");
        this.status = Objects.requireNonNull(status, "Review status is required");
        this.managerJustification = managerJustification;
        this.hrJustification = hrJustification;
    }

    public void recalculateScore(PerformanceScore recalculatedScore, String justification) {
        assertDraftState();
        this.score = Objects.requireNonNull(recalculatedScore, "Performance score is required");
        this.managerJustification = justification;
    }

    public void submitByManager() {
        assertDraftState();
        status = PerformanceReviewStatus.MANAGER_SUBMITTED;
    }

    public void calibrateByHr(PerformanceScore calibratedScore, String justification) {
        if (status != PerformanceReviewStatus.MANAGER_SUBMITTED) {
            throw new IllegalStateException("Review must be manager-submitted before HR calibration");
        }
        score = Objects.requireNonNull(calibratedScore, "Calibrated score is required");
        hrJustification = justification;
        status = PerformanceReviewStatus.HR_CALIBRATED;
    }

    public void finalizeByLeadership() {
        if (status != PerformanceReviewStatus.HR_CALIBRATED) {
            throw new IllegalStateException("Review must be HR-calibrated before finalization");
        }
        status = PerformanceReviewStatus.FINALIZED;
    }

    private void assertDraftState() {
        if (status != PerformanceReviewStatus.DRAFT) {
            throw new IllegalStateException("Review can only be modified in draft state");
        }
    }

    public Long reviewId() { return reviewId; }
    public EmployeeId employeeId() { return employeeId; }
    public ReviewCycleId reviewCycleId() { return reviewCycleId; }
    public PerformanceScore score() { return score; }
    public PerformanceReviewStatus status() { return status; }
    public String managerJustification() { return managerJustification; }
    public String hrJustification() { return hrJustification; }
}
