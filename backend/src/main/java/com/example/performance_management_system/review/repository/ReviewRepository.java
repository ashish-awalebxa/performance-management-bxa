package com.example.performance_management_system.review.repository;

import com.example.performance_management_system.review.model.Review;
import com.example.performance_management_system.reviewcycle.model.ReviewCycle;
import com.example.performance_management_system.reviewcycle.model.ReviewCycleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByEmployeeIdAndReviewCycle_Status(
            Long employeeId,
            ReviewCycleStatus status
    );

    List<Review> findByManagerIdAndReviewCycle_StatusOrderByCreatedAtDesc(
            Long managerId,
            ReviewCycleStatus cycleStatus
    );

    boolean existsByEmployeeIdAndReviewCycle(
            Long employeeId,
            ReviewCycle reviewCycle
    );

    Page<Review> findByManagerId(Long managerId, Pageable pageable);
}
