package com.example.performance_management_system.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SubmitReviewRequest {

    @NotBlank(message = "Review comments cannot be empty")
    @Size(max = 2000, message = "Comments too long")
    public String comments;
}
