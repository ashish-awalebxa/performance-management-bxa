package com.example.performance_management_system.performancecycle.model;

import com.example.performance_management_system.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "performance_cycle")
@Getter
@Setter
public class PerformanceCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cycleId;

    private String cycleName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDate createdDate;
    private LocalDate reviewDeadline;
    private String feedbackWindow;
}

