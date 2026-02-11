package com.example.performance_management_system.rating.model;

import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "rating",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_rating_employee_cycle",
                        columnNames = {"employee_id", "performance_cycle_id"}
                )
        }
)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Transient
    private String employeeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_cycle_id", nullable = false)
    private PerformanceCycle performanceCycle;

    @Column(nullable = false)
    private Double score; // e.g. 1.0 – 5.0

    @Enumerated(EnumType.STRING)
    private RatingStatus status;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    private String managerJustification;
    private String hrJustification;

    private LocalDateTime createdAt;

    /* ---------- Domain Rules ---------- */

    public void submitByManager() {
        if (status != RatingStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT ratings can be submitted");
        }
        status = RatingStatus.MANAGER_SUBMITTED;
    }

    public void calibrateByHr(Double newScore, String justification) {
        if (status != RatingStatus.MANAGER_SUBMITTED) {
            throw new IllegalStateException("Only MANAGER_SUBMITTED ratings can be calibrated");
        }
        this.score = newScore;
        this.hrJustification = justification;
        status = RatingStatus.HR_CALIBRATED;
    }

    public void finalizeRating() {
        if (status != RatingStatus.HR_CALIBRATED) {
            throw new IllegalStateException("Only HR_CALIBRATED ratings can be finalized");
        }
        status = RatingStatus.FINALIZED;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        status = RatingStatus.DRAFT;
    }

    // getters & setters
}
