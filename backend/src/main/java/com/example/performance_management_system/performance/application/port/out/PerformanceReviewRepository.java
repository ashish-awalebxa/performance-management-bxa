package com.example.performance_management_system.performance.application.port.out;

import com.example.performance_management_system.performance.domain.model.entity.PerformanceReview;
import com.example.performance_management_system.performance.domain.model.valueobject.EmployeeId;
import com.example.performance_management_system.performance.domain.model.valueobject.ReviewCycleId;

import java.util.Optional;

public interface PerformanceReviewRepository {
    Optional<PerformanceReview> findByEmployeeAndCycle(EmployeeId employeeId, ReviewCycleId reviewCycleId);
    PerformanceReview save(PerformanceReview review);
}
