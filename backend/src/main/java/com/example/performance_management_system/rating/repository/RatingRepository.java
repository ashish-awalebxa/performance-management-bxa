package com.example.performance_management_system.rating.repository;

import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByEmployeeIdAndPerformanceCycle(
            Long employeeId,
            PerformanceCycle performanceCycle
    );

    Page<Rating> findByPerformanceCycle_Id(Long cycleId, Pageable pageable);
}
