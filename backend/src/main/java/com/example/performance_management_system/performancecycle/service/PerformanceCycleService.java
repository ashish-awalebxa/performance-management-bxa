package com.example.performance_management_system.performancecycle.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.performancecycle.model.CycleStatus;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.performancecycle.repository.PerformanceCycleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerformanceCycleService {

    private final PerformanceCycleRepository repository;

    public PerformanceCycleService(PerformanceCycleRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public PerformanceCycle createCycle(PerformanceCycle cycle) {

        if (cycle.getEndDate().isBefore(cycle.getStartDate())) {
            throw new BusinessException("End date cannot be before start date");
        }

        return repository.save(cycle);
    }

    @Transactional
    public PerformanceCycle startCycle(Long cycleId) {

        PerformanceCycle cycle = repository.findById(cycleId)
                .orElseThrow(() -> new BusinessException("Cycle not found"));

        if (repository.existsByStatusAndCycleType(
                CycleStatus.ACTIVE, cycle.getCycleType())) {
            throw new BusinessException(
                    "An ACTIVE cycle already exists for this type"
            );
        }

        cycle.activate();
        return repository.save(cycle);
    }

    @Transactional
    public PerformanceCycle closeCycle(Long cycleId) {

        PerformanceCycle cycle = repository.findById(cycleId)
                .orElseThrow(() -> new BusinessException("Cycle not found"));

        cycle.close();
        return repository.save(cycle);
    }

    public PerformanceCycle getActiveCycle() {
        return repository.findByStatus(CycleStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("No active cycle"));
    }

    public List<PerformanceCycle> getAllCycles() {
        return repository.findAll();
    }

}

