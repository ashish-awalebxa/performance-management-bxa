package com.example.performance_management_system.goal.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectGoalRequest {

    @NotBlank(message = "Rejection reason is required")
    public String reason;
}
