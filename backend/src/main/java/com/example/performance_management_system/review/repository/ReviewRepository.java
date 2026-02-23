package com.example.performance_management_system.review.repository;


import com.example.performance_management_system.review.model.Review;
import com.example.performance_management_system.review.model.ReviewStatus;
import com.example.performance_management_system.reviewcycle.model.ReviewCycle;
import com.example.performance_management_system.reviewcycle.model.ReviewCycleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByEmployeeIdAndReviewCycle_Status(
            Long employeeId,
            ReviewCycleStatus status
    );

    List<Review> findByManagerIdAndStatusAndReviewCycle_Status(
            Long managerId,
            ReviewStatus status,
            ReviewCycleStatus cycleStatus
    );

    boolean existsByEmployeeIdAndReviewCycle(
            Long employeeId,
            ReviewCycle reviewCycle
    );

    @Query("""
    SELECT r
    FROM Review r
    WHERE r.managerId = :managerId
      AND r.status = :status
""")
    List<Review> findTeamReviews(
            @Param("managerId") Long managerId,
            @Param("status") ReviewStatus status
    );










    Page<Review> findByManagerId(Long managerId, Pageable pageable);
}
