package com.example.performance_management_system.reviewcycle.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.performancecycle.service.PerformanceCycleService;
import com.example.performance_management_system.reviewcycle.model.ReviewCycle;
import com.example.performance_management_system.reviewcycle.repository.ReviewCycleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewCycleService {

    private final ReviewCycleRepository repository;
    private final PerformanceCycleService performanceCycleService;

    public ReviewCycleService(ReviewCycleRepository repository,
                              PerformanceCycleService performanceCycleService) {
        this.repository = repository;
        this.performanceCycleService = performanceCycleService;
    }

    @Transactional
    public ReviewCycle create(ReviewCycle cycle) {
        cycle.setPerformanceCycle(performanceCycleService.getActiveCycle());
        return repository.save(cycle);
    }

    @Transactional
    public ReviewCycle activate(Long id) {
        ReviewCycle cycle = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Review cycle not found"));

        cycle.activate();
        return repository.save(cycle);
    }

    @Transactional
    public ReviewCycle close(Long id) {
        ReviewCycle cycle = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Review cycle not found"));

        cycle.close();
        return repository.save(cycle);
    }
}

