package com.example.performance_management_system.reviewcycle.repository;

import com.example.performance_management_system.reviewcycle.model.ReviewCycle;
import com.example.performance_management_system.reviewcycle.model.ReviewCycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewCycleRepository extends JpaRepository<ReviewCycle, Long> {

    Optional<ReviewCycle> findByStatus(ReviewCycleStatus status);
}
