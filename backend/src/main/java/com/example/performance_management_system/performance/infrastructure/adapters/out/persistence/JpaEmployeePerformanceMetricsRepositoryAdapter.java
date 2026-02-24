package com.example.performance_management_system.performance.infrastructure.adapters.out.persistence;

import com.example.performance_management_system.goal.model.GoalStatus;
import com.example.performance_management_system.goal.repository.GoalRepository;
import com.example.performance_management_system.performance.application.port.out.EmployeePerformanceMetricsRepository;
import com.example.performance_management_system.performance.domain.model.valueobject.EmployeeId;
import com.example.performance_management_system.performance.domain.model.valueobject.GoalProgress;
import com.example.performance_management_system.performance.domain.model.valueobject.GoalStatusSnapshot;
import com.example.performance_management_system.performance.domain.model.valueobject.KeyResultProgress;
import com.example.performance_management_system.performance.domain.model.valueobject.ReviewCycleId;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaEmployeePerformanceMetricsRepositoryAdapter implements EmployeePerformanceMetricsRepository {

    private final GoalRepository goalRepository;

    public JpaEmployeePerformanceMetricsRepositoryAdapter(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Override
    public List<GoalProgress> loadGoalProgress(EmployeeId employeeId, ReviewCycleId reviewCycleId) {
        return goalRepository.findByEmployeeIdAndPerformanceCycle_Id(employeeId.value(), reviewCycleId.value())
                .stream()
                .map(goal -> new GoalProgress(
                        mapStatus(goal.getStatus()),
                        goal.getKeyResults().stream()
                                .map(kr -> new KeyResultProgress(
                                        kr.getCurrentValue() == null ? 0.0 : kr.getCurrentValue(),
                                        kr.getTargetValue() == null ? 0.0 : kr.getTargetValue()
                                ))
                                .toList()
                ))
                .toList();
    }

    private GoalStatusSnapshot mapStatus(GoalStatus status) {
        return GoalStatusSnapshot.valueOf(status.name());
    }
}
