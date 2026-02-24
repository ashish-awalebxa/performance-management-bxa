package com.example.performance_management_system.performance.application.dto;

import com.example.performance_management_system.performance.domain.model.valueobject.RatingCriteria;

public record CalculatePerformanceScoreCommand(Long employeeId, Long reviewCycleId, RatingCriteria criteria) {
}
