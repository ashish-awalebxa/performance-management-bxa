package com.example.performance_management_system.rating.model;

import com.example.performance_management_system.department.model.Department;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rating")
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long finalRatingId;

    @ManyToOne
    @JoinColumn(name = "cycle_id")
    private PerformanceCycle performanceCycle;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @ManyToOne
    @JoinColumn(name = "skip_manager_id")
    private User skipManager;

    private BigDecimal finalRatingValue;
    private String performanceCategory;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "locked_by")
    private User lockedBy;

    private LocalDateTime lockedAt;
    private LocalDateTime createdAt;
}

