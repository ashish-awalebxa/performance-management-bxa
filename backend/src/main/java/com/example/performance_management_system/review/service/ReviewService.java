package com.example.performance_management_system.review.service;

import com.example.performance_management_system.common.error.ErrorCode;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.review.dto.ReviewResponse;
import com.example.performance_management_system.review.model.Review;
import com.example.performance_management_system.review.repository.ReviewRepository;
import com.example.performance_management_system.reviewcycle.model.ReviewCycleStatus;
import com.example.performance_management_system.reviewcycle.repository.ReviewCycleRepository;
import com.example.performance_management_system.user.service.HierarchyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewCycleRepository reviewCycleRepository;
    private final HierarchyService hierarchyService;

    public ReviewService(
            ReviewRepository reviewRepository,
            ReviewCycleRepository reviewCycleRepository,
            HierarchyService hierarchyService
    ) {
        this.reviewRepository = reviewRepository;
        this.reviewCycleRepository = reviewCycleRepository;
        this.hierarchyService = hierarchyService;
    }

    @Transactional
    public Review createReview(Long employeeId, Long managerId, Long reviewCycleId) {

        Review review = new Review();
        review.setEmployeeId(employeeId);
        review.setManagerId(managerId);
        review.setReviewCycle(
                reviewCycleRepository.findById(reviewCycleId)
                        .orElseThrow(() -> new BusinessException(
                                HttpStatus.NOT_FOUND,
                                ErrorCode.SYSTEM_ERROR, // see note below
                                "Review cycle not found"
                        ))
        );

        return reviewRepository.save(review);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Transactional
    public Review submitSelfReview(Long reviewId, String comments) {

        Review review = get(reviewId);

        if (!review.getEmployeeId().equals(SecurityUtil.userId())) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.ACCESS_DENIED,
                    "You can submit self-review only for yourself"
            );
        }

        if (review.getReviewCycle().getStatus() != ReviewCycleStatus.ACTIVE) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.GOAL_INVALID_STATE,
                    "Review cycle is not active"
            );
        }

        if (!review.getReviewCycle().getSelfReviewEnabled()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.GOAL_INVALID_STATE,
                    "Self review is disabled for this cycle"
            );
        }

        review.submitSelfReview();
        review.setSelfReviewComments(comments);

        return reviewRepository.save(review);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public Review submitManagerReview(Long reviewId, String comments) {

        Review review = get(reviewId);

        if (!review.getReviewCycle().getManagerReviewEnabled()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.GOAL_INVALID_STATE,
                    "Manager review is disabled for this cycle"
            );
        }

        Long managerId = SecurityUtil.userId();
        String role = SecurityUtil.role();

        if (!role.equals("HR") && !role.equals("ADMIN")) {
            hierarchyService.validateManagerAccess(
                    managerId,
                    review.getEmployeeId()
            );
        }

        if (review.getReviewCycle().getStatus() != ReviewCycleStatus.ACTIVE) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.GOAL_INVALID_STATE,
                    "Review cycle is not active"
            );
        }

        review.submitManagerReview();
        review.setManagerReviewComments(comments);

        return reviewRepository.save(review);
    }

    private Review get(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.REVIEW_NOT_FOUND,
                        "Review not found"
                ));
    }

    public Page<Review> getReviewsForManager(
            Long managerId,
            int page,
            int size
    ) {
        return reviewRepository.findByManagerId(
                managerId,
                PageRequest.of(page, size)
        );
    }

    public Optional<Review> getMyActiveReview(Long employeeId) {
        return reviewRepository.findByEmployeeIdAndReviewCycle_Status(
                employeeId,
                ReviewCycleStatus.ACTIVE
        );
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getTeamReviews(Long managerId) {

        List<Review> reviews = reviewRepository.findByManagerIdAndReviewCycle_StatusOrderByCreatedAtDesc(
                managerId,
                ReviewCycleStatus.ACTIVE
        );

        return reviews.stream().map(review -> {
            ReviewResponse r = new ReviewResponse();
            r.id = review.getId();
            r.employeeId = review.getEmployeeId();
            r.managerId = review.getManagerId();
            r.status = review.getStatus().name();
            r.selfReviewComments = review.getSelfReviewComments();
            r.managerReviewComments = review.getManagerReviewComments();
            r.reviewCycleId = review.getReviewCycle().getId();
            return r;
        }).toList();
    }
}
