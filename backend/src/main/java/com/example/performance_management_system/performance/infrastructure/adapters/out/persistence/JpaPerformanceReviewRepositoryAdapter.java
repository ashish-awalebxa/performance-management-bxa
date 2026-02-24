package com.example.performance_management_system.performance.infrastructure.adapters.out.persistence;

import com.example.performance_management_system.performance.application.port.out.PerformanceReviewRepository;
import com.example.performance_management_system.performance.domain.model.entity.PerformanceReview;
import com.example.performance_management_system.performance.domain.model.entity.PerformanceReviewStatus;
import com.example.performance_management_system.performance.domain.model.valueobject.EmployeeId;
import com.example.performance_management_system.performance.domain.model.valueobject.PerformanceScore;
import com.example.performance_management_system.performance.domain.model.valueobject.ReviewCycleId;
import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.rating.model.RatingStatus;
import com.example.performance_management_system.rating.repository.RatingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaPerformanceReviewRepositoryAdapter implements PerformanceReviewRepository {

    private final RatingRepository ratingRepository;

    public JpaPerformanceReviewRepositoryAdapter(
            RatingRepository ratingRepository
    ) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Optional<PerformanceReview> findByEmployeeAndCycle(EmployeeId employeeId, ReviewCycleId reviewCycleId) {
        return ratingRepository.findByEmployeeIdAndPerformanceCycle_Id(employeeId.value(), reviewCycleId.value())
                .map(this::toDomain);
    }

    @Override
    public PerformanceReview save(PerformanceReview review) {
        Rating rating = ratingRepository.findById(review.reviewId())
                .orElseThrow(() -> new IllegalArgumentException("Rating not found for id " + review.reviewId()));

        rating.setScore(review.score().value());
        rating.setStatus(RatingStatus.valueOf(review.status().name()));
        rating.setManagerJustification(review.managerJustification());
        rating.setHrJustification(review.hrJustification());

        Rating saved = ratingRepository.save(rating);
        return toDomain(saved);
    }

    private PerformanceReview toDomain(Rating rating) {
        return new PerformanceReview(
                rating.getId(),
                new EmployeeId(rating.getEmployeeId()),
                new ReviewCycleId(rating.getPerformanceCycle().getId()),
                new PerformanceScore(rating.getScore()),
                PerformanceReviewStatus.valueOf(rating.getStatus().name()),
                rating.getManagerJustification(),
                rating.getHrJustification()
        );
    }
}
