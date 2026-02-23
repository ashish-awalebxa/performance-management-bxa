package com.example.performance_management_system.rating.repository;

import com.example.performance_management_system.performancecycle.model.CycleStatus;
import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.rating.model.RatingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByEmployeeIdAndPerformanceCycle(
            Long employeeId,
            PerformanceCycle performanceCycle
    );

    boolean existsByEmployeeIdAndPerformanceCycle(
            Long employeeId,
            PerformanceCycle performanceCycle
    );

    List<Rating> findByManagerId(Long managerId);

    List<Rating> findByManagerIdAndPerformanceCycle(
            Long managerId,
            PerformanceCycle performanceCycle
    );

    List<Rating> findByStatusAndPerformanceCycle(
            RatingStatus status,
            PerformanceCycle performanceCycle
    );

    List<Rating> findByPerformanceCycle(PerformanceCycle cycle);


    Page<Rating> findByPerformanceCycle_Id(Long cycleId, Pageable pageable);

    Page<Rating> findByManagerIdAndPerformanceCycle_Id(
            Long managerId,
            Long cycleId,
            Pageable pageable
    );

    Optional<Rating> findByEmployeeIdAndPerformanceCycle_Status(
            Long employeeId,
            CycleStatus status
    );


    Optional<Rating> findByEmployeeIdAndStatus(
            Long employeeId,
            RatingStatus status
    );

    List<Rating> findByManagerIdAndStatus(
            Long managerId,
            RatingStatus status
    );


}
