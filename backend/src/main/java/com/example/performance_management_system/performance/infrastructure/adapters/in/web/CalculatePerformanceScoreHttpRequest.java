package com.example.performance_management_system.performance.infrastructure.adapters.in.web;

import com.example.performance_management_system.performance.domain.model.valueobject.RatingCriteria;

public record CalculatePerformanceScoreHttpRequest(Long employeeId, Long reviewCycleId, RatingCriteria criteria) {
}
