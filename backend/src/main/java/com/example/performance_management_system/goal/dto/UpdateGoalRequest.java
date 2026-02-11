package com.example.performance_management_system.goal.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateGoalRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    public String title;

    @Size(max = 1000, message = "Description too long")
    public String description;

    @NotNull(message = "At least one key result is required")
    @Size(min = 1, message = "At least one key result is required")
    public List<@Valid UpsertKeyResultRequest> keyResults;
}
