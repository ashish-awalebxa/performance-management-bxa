package com.example.performance_management_system.rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CalibrateRatingRequest {

    @Min(value = 1, message = "Minimum rating is 1")
    @Max(value = 5, message = "Maximum rating is 5")
    public Double newScore;

    @NotBlank(message = "Calibration justification is required")
    public String justification;
}
