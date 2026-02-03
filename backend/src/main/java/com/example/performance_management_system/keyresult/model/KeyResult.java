package com.example.performance_management_system.keyresult.model;

import com.example.performance_management_system.goal.model.Goal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "key_result")
public class KeyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @Column(nullable = false)
    private String metric;

    @Column(nullable = false)
    private Double targetValue;

    @Column(nullable = false)
    private Double currentValue = 0.0;

    public void updateProgress(Double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Progress cannot be negative");
        }
        this.currentValue = value;
    }

    // getters & setters
}
