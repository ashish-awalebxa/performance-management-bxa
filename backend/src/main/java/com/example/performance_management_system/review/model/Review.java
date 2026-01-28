package com.example.performance_management_system.review.model;

import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.reviewcycle.model.ReviewCycle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(
        name = "review",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_employee_cycle",
                        columnNames = {"employee_id", "review_cycle_id"}
                )
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_cycle_id", nullable = false)
    private ReviewCycle reviewCycle;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @Lob
    private String selfReviewComments;

    @Lob
    private String managerReviewComments;

    private LocalDateTime createdAt;

    /* ---------- Domain Rules ---------- */

    public void submitSelfReview() {
        if (status != ReviewStatus.NOT_STARTED) {
            throw new IllegalStateException("Self-review already submitted");
        }
        status = ReviewStatus.SELF_REVIEW_SUBMITTED;
    }

    public void submitManagerReview() {
        if (status != ReviewStatus.SELF_REVIEW_SUBMITTED) {
            throw new IllegalStateException("Manager review not allowed yet");
        }
        status = ReviewStatus.MANAGER_REVIEW_SUBMITTED;
    }

    public void finalizeReview() {
        if (status != ReviewStatus.MANAGER_REVIEW_SUBMITTED) {
            throw new IllegalStateException("Review not ready to finalize");
        }
        status = ReviewStatus.FINALIZED;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        status = ReviewStatus.NOT_STARTED;
    }

    // getters & setters
}
