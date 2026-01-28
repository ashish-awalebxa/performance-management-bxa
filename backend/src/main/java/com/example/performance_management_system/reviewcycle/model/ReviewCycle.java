package com.example.performance_management_system.reviewcycle.model;

import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "review_cycle")
public class ReviewCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_cycle_id", nullable = false)
    private PerformanceCycle performanceCycle;

    @Column(nullable = false)
    private String name;

    private Boolean selfReviewEnabled;
    private Boolean managerReviewEnabled;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ReviewCycleStatus status;

    private LocalDateTime createdAt;

    /* ---------- Domain Rules ---------- */

    public void activate() {
        if (status != ReviewCycleStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT review cycles can be activated");
        }
        status = ReviewCycleStatus.ACTIVE;
    }

    public void close() {
        if (status != ReviewCycleStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE review cycles can be closed");
        }
        status = ReviewCycleStatus.CLOSED;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        status = ReviewCycleStatus.DRAFT;
    }

    // getters & setters
}
