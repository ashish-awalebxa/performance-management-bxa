package com.example.performance_management_system.goal.model;


import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_cycle_id", nullable = false)
    private PerformanceCycle performanceCycle;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status;

    @Column(length = 1000)
    private String rejectionReason;

    private LocalDateTime rejectedAt;

    @OneToMany(
            mappedBy = "goal",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<com.example.performance_management_system.keyresult.model.KeyResult> keyResults = new ArrayList<>();

    private LocalDateTime createdAt;

    /* ---------- Domain Rules ---------- */

    public void submit() {
        if (status != GoalStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT goals can be submitted");
        }
        status = GoalStatus.SUBMITTED;
    }

    public void approve() {
        if (status != GoalStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED goals can be approved");
        }
        status = GoalStatus.APPROVED;
    }

    public void reject(String reason) {
        if (status != GoalStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED goals can be rejected");
        }
        this.status = GoalStatus.REJECTED;
        this.rejectionReason = reason;
        this.rejectedAt = LocalDateTime.now();
    }


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = GoalStatus.DRAFT;
    }

    // getters & setters
}
