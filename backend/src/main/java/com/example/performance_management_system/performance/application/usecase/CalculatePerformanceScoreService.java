package com.example.performance_management_system.performance.application.usecase;

import com.example.performance_management_system.performance.application.dto.CalculatePerformanceScoreCommand;
import com.example.performance_management_system.performance.application.dto.PerformanceScoreResult;
import com.example.performance_management_system.performance.application.port.in.CalculatePerformanceScoreUseCase;
import com.example.performance_management_system.performance.application.port.out.EmployeePerformanceMetricsRepository;
import com.example.performance_management_system.performance.application.port.out.PerformanceReviewRepository;
import com.example.performance_management_system.performance.domain.model.entity.PerformanceReview;
import com.example.performance_management_system.performance.domain.model.valueobject.EmployeeId;
import com.example.performance_management_system.performance.domain.model.valueobject.PerformanceScore;
import com.example.performance_management_system.performance.domain.model.valueobject.RatingCriteria;
import com.example.performance_management_system.performance.domain.model.valueobject.ReviewCycleId;
import com.example.performance_management_system.performance.domain.service.PerformanceScoreCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalculatePerformanceScoreService implements CalculatePerformanceScoreUseCase {

    private final EmployeePerformanceMetricsRepository metricsRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final PerformanceScoreCalculator scoreCalculator;

    public CalculatePerformanceScoreService(
            EmployeePerformanceMetricsRepository metricsRepository,
            PerformanceReviewRepository performanceReviewRepository
    ) {
        this.metricsRepository = metricsRepository;
        this.performanceReviewRepository = performanceReviewRepository;
        this.scoreCalculator = new PerformanceScoreCalculator();
    }

    @Override
    @Transactional
    public PerformanceScoreResult calculate(CalculatePerformanceScoreCommand command) {
        EmployeeId employeeId = new EmployeeId(command.employeeId());
        ReviewCycleId cycleId = new ReviewCycleId(command.reviewCycleId());
        RatingCriteria criteria = command.criteria() == null ? RatingCriteria.standard() : command.criteria();

        PerformanceScore score = scoreCalculator.calculate(
                metricsRepository.loadGoalProgress(employeeId, cycleId),
                criteria
        );

        PerformanceReview review = performanceReviewRepository.findByEmployeeAndCycle(employeeId, cycleId)
                .orElseThrow(() -> new IllegalArgumentException("Performance review not found for employee in review cycle"));

        review.recalculateScore(score, "Calculated from goals via domain service");
        performanceReviewRepository.save(review);

        return new PerformanceScoreResult(employeeId.value(), cycleId.value(), score.value());
    }
}
