package com.example.performance_management_system.goal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class KeyResultRequest {

    @NotBlank(message = "Metric is required")
    public String metric;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target value must be positive")
    public Double targetValue;
}
