package com.example.performance_management_system.feedback.dto;

import com.example.performance_management_system.feedback.model.FeedbackType;
import com.example.performance_management_system.feedback.model.FeedbackVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateFeedbackRequest {

    @NotNull
    public Long recipientId;

    @NotNull
    public FeedbackType type;

    @NotNull
    public FeedbackVisibility visibility;

    @NotBlank
    @Size(max = 120)
    public String title;

    @NotBlank
    @Size(max = 2000)
    public String message;

    @Size(max = 600)
    public String actionItems;
}
