package com.example.performance_management_system.rating.dto;

import jakarta.validation.constraints.NotNull;

public class CreateRatingRequest {

    @NotNull(message = "Employee ID is required")
    public Long employeeId;

    public Double score;
    public String managerJustification;
}
