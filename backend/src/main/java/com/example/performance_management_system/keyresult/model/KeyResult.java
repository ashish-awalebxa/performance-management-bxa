package com.example.performance_management_system.keyresult.model;

import com.example.performance_management_system.goal.model.Goal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "key_results")
@Getter
@Setter
public class KeyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    private String title;
    private String metric;

    private BigDecimal target;
    private BigDecimal progress;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

