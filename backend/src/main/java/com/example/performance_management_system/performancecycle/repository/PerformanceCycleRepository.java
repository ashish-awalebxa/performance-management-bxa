package com.example.performance_management_system.performancecycle.repository;

import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.performancecycle.model.CycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerformanceCycleRepository
        extends JpaRepository<PerformanceCycle, Long> {

    Optional<PerformanceCycle> findByStatus(CycleStatus status);

    boolean existsByStatusAndCycleType(CycleStatus status, String cycleType);
}
