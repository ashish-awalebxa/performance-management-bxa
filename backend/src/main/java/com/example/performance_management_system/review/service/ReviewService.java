package com.example.performance_management_system.review.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.review.model.Review;
import com.example.performance_management_system.review.repository.ReviewRepository;
import com.example.performance_management_system.reviewcycle.model.ReviewCycleStatus;
import com.example.performance_management_system.reviewcycle.repository.ReviewCycleRepository;
import com.example.performance_management_system.user.service.HierarchyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository repository;
    private final ReviewCycleRepository reviewCycleRepository;
    private final HierarchyService hierarchyService;
    public ReviewService(ReviewRepository repository,
                         ReviewCycleRepository reviewCycleRepository,
                         HierarchyService hierarchyService) {
        this.repository = repository;
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
                        .orElseThrow(() -> new BusinessException("Review cycle not found"))
        );

        return repository.save(review);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Transactional
    public Review submitSelfReview(Long reviewId, String comments) {

        Review review = get(reviewId);

        if (!review.getEmployeeId().equals(SecurityUtil.userId())) {
            throw new BusinessException("You can submit self-review only for yourself");
        }

        if(review.getReviewCycle().getStatus()!= ReviewCycleStatus.ACTIVE){
            throw new BusinessException("Review Cycle is not Active");
        }

        review.submitSelfReview();
        review.setSelfReviewComments(comments);


        return repository.save(review);
    }


    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public Review submitManagerReview(Long reviewId, String comments) {

        Review review = get(reviewId);

        Long managerId = SecurityUtil.userId();
        String role = SecurityUtil.role();

        if (!role.equals("HR") && !role.equals("ADMIN")) {
            hierarchyService.validateManagerAccess(
                    managerId,
                    review.getEmployeeId()
            );
        }

        if(review.getReviewCycle().getStatus()!= ReviewCycleStatus.ACTIVE){
            throw new BusinessException("Review cycle is not Active");
        }

        review.submitManagerReview();
        review.setManagerReviewComments(comments);


        return repository.save(review);
    }


    private Review get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Review not found"));
    }

    public Page<Review> getReviewsForManager(
            Long managerId,
            int page,
            int size
    ) {
        return repository.findByManagerId(
                managerId,
                PageRequest.of(page, size)
        );
    }

}

