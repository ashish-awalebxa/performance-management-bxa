package com.example.performance_management_system.performance.application.port.out;

import com.example.performance_management_system.performance.domain.model.valueobject.EmployeeId;
import com.example.performance_management_system.performance.domain.model.valueobject.GoalProgress;
import com.example.performance_management_system.performance.domain.model.valueobject.ReviewCycleId;

import java.util.List;

public interface EmployeePerformanceMetricsRepository {
    List<GoalProgress> loadGoalProgress(EmployeeId employeeId, ReviewCycleId reviewCycleId);
}
