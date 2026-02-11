package com.example.performance_management_system.feedback.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "continuous_feedback")
public class ContinuousFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long giverId;

    @Column(nullable = false)
    private Long recipientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackVisibility visibility;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(length = 600)
    private String actionItems;

    @Column(nullable = false)
    private Boolean acknowledged;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acknowledgedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.acknowledged = Boolean.FALSE;
    }
}
