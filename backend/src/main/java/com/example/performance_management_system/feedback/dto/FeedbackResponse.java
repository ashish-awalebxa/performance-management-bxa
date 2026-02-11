package com.example.performance_management_system.feedback.dto;

import com.example.performance_management_system.feedback.model.FeedbackType;
import com.example.performance_management_system.feedback.model.FeedbackVisibility;

import java.time.LocalDateTime;

public class FeedbackResponse {
    public Long id;
    public Long giverId;
    public Long recipientId;
    public FeedbackType type;
    public FeedbackVisibility visibility;
    public String title;
    public String message;
    public String actionItems;
    public Boolean acknowledged;
    public LocalDateTime acknowledgedAt;
    public LocalDateTime createdAt;
}
