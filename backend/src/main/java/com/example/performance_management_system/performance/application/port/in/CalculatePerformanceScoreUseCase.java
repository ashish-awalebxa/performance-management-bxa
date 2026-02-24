package com.example.performance_management_system.performance.application.port.in;

import com.example.performance_management_system.performance.application.dto.CalculatePerformanceScoreCommand;
import com.example.performance_management_system.performance.application.dto.PerformanceScoreResult;

public interface CalculatePerformanceScoreUseCase {
    PerformanceScoreResult calculate(CalculatePerformanceScoreCommand command);
}
