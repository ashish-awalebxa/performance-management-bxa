package com.example.performance_management_system.performancecycle.service;

import com.example.performance_management_system.common.error.ErrorCode;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.performancecycle.model.CycleStatus;
import com.example.performance_management_system.reviewcycle.model.ReviewCycleStatus;
import com.example.performance_management_system.reviewcycle.repository.ReviewCycleRepository;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.performancecycle.repository.PerformanceCycleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PerformanceCycleService {

    private final PerformanceCycleRepository repository;
    private final ReviewCycleRepository reviewCycleRepository;

    public PerformanceCycleService(
            PerformanceCycleRepository repository,
            ReviewCycleRepository reviewCycleRepository
    ) {
        this.repository = repository;
        this.reviewCycleRepository = reviewCycleRepository;
    }
    @Transactional
    public PerformanceCycle createCycle(PerformanceCycle cycle) {

        if (cycle.getEndDate().isBefore(cycle.getStartDate())) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.VALIDATION_FAILED,
                    "End date cannot be before start date"
            );
        }

        return repository.save(cycle);
    }

    @Transactional
    public PerformanceCycle startCycle(Long cycleId) {

        PerformanceCycle cycle = repository.findById(cycleId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.CYCLE_ALREADY_ACTIVE, // see note below
                        "Performance cycle not found"
                ));

        if (repository.existsByStatusAndCycleType(
                CycleStatus.ACTIVE, cycle.getCycleType())) {

            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.CYCLE_ALREADY_ACTIVE,
                    "An ACTIVE cycle already exists for this cycle type"
            );
        }

        cycle.activate();
        return repository.save(cycle);
    }

    @Transactional
    public PerformanceCycle closeCycle(Long cycleId) {

        PerformanceCycle cycle = repository.findById(cycleId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SYSTEM_ERROR,
                        "Performance cycle not found"
                ));
        if (reviewCycleRepository.existsByStatusAndPerformanceCycle_Id(
                ReviewCycleStatus.ACTIVE,
                cycleId
        )) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.VALIDATION_FAILED,
                    "Cannot close performance cycle while a review cycle is active"
            );
        }

        cycle.close();
        return repository.save(cycle);
    }

    public PerformanceCycle getActiveCycle() {

        return repository.findByStatus(CycleStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SYSTEM_ERROR,
                        "No active performance cycle found"
                ));
    }

    public List<PerformanceCycle> getAllCycles() {
        return repository.findAll();
    }
}
