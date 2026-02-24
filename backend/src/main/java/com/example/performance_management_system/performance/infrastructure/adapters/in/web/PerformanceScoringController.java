package com.example.performance_management_system.performance.infrastructure.adapters.in.web;

import com.example.performance_management_system.performance.application.dto.CalculatePerformanceScoreCommand;
import com.example.performance_management_system.performance.application.dto.PerformanceScoreResult;
import com.example.performance_management_system.performance.application.port.in.CalculatePerformanceScoreUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/performance-scores")
public class PerformanceScoringController {

    private final CalculatePerformanceScoreUseCase calculatePerformanceScoreUseCase;

    public PerformanceScoringController(CalculatePerformanceScoreUseCase calculatePerformanceScoreUseCase) {
        this.calculatePerformanceScoreUseCase = calculatePerformanceScoreUseCase;
    }

    @PostMapping("/calculate")
    public PerformanceScoreResult calculate(@RequestBody CalculatePerformanceScoreHttpRequest request) {
        return calculatePerformanceScoreUseCase.calculate(
                new CalculatePerformanceScoreCommand(request.employeeId(), request.reviewCycleId(), request.criteria())
        );
    }
}
