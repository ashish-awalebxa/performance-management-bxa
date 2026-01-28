package com.example.performance_management_system.goal.repository;

import com.example.performance_management_system.goal.model.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    Page<Goal> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<Goal> findByPerformanceCycle_Id(Long cycleId, Pageable pageable);
}
