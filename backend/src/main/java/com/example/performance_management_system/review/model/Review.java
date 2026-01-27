package com.example.performance_management_system.review.model;

import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.reviewcycle.model.ReviewCycle;
import com.example.performance_management_system.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cycle_id")
    private PerformanceCycle performanceCycle;

    @ManyToOne
    @JoinColumn(name = "review_cycles_id")
    private ReviewCycle reviewCycle;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    private BigDecimal status;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "final_rating_id")
    private Rating rating;
}

