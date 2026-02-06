package com.example.performance_management_system.rating.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UpdateManagerRatingRequest {

    @NotNull
    @Positive
    public Integer score;

    @Size(max = 1000)
    public String justification;
}
