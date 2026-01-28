package com.example.performance_management_system.performancecycle.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "performance_cycle",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cycle_name_type",
                        columnNames = {"name", "cycle_type"}
                )
        }
)
public class PerformanceCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "cycle_type", nullable = false)
    private String cycleType; // ANNUAL / QUARTERLY

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CycleStatus status;

    @Column(nullable = false)
    private String createdBy;

    private LocalDateTime createdAt;

    /* ---------- Domain Methods (IMPORTANT) ---------- */

    public void activate() {
        if (this.status != CycleStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT cycles can be activated");
        }
        this.status = CycleStatus.ACTIVE;
    }

    public void close() {
        if (this.status != CycleStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE cycles can be closed");
        }
        this.status = CycleStatus.CLOSED;
    }

    /* ---------- JPA Hooks ---------- */

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = CycleStatus.DRAFT;
    }

    // getters & setters omitted for brevity (generate them)
}

