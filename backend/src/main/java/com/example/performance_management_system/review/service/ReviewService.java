package com.example.performance_management_system.review.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.review.model.Review;
import com.example.performance_management_system.review.repository.ReviewRepository;
import com.example.performance_management_system.reviewcycle.repository.ReviewCycleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository repository;
    private final ReviewCycleRepository reviewCycleRepository;

    public ReviewService(ReviewRepository repository,
                         ReviewCycleRepository reviewCycleRepository) {
        this.repository = repository;
        this.reviewCycleRepository = reviewCycleRepository;
    }

    @Transactional
    public Review createReview(Long employeeId, Long managerId, Long reviewCycleId) {

        Review review = new Review();
        review.setEmployeeId(employeeId);
        review.setManagerId(managerId);
        review.setReviewCycle(
                reviewCycleRepository.findById(reviewCycleId)
                        .orElseThrow(() -> new BusinessException("Review cycle not found"))
        );

        return repository.save(review);
    }

    @Transactional
    public Review submitSelfReview(Long reviewId, String comments) {
        Review review = get(reviewId);
        review.setSelfReviewComments(comments);
        review.submitSelfReview();
        return repository.save(review);
    }

    @Transactional
    public Review submitManagerReview(Long reviewId, String comments) {
        Review review = get(reviewId);
        review.setManagerReviewComments(comments);
        review.submitManagerReview();
        return repository.save(review);
    }

    private Review get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Review not found"));
    }
}

