package com.example.performance_management_system.review.controller;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.review.dto.ReviewResponse;
import com.example.performance_management_system.review.dto.SubmitReviewRequest;
import com.example.performance_management_system.review.model.Review;
import com.example.performance_management_system.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    /* ================= EMPLOYEE ================= */

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<?> getMyReview() {

        Long employeeId = SecurityUtil.userId();

        return service.getMyActiveReview(employeeId)
                .map(review -> ResponseEntity.ok(review))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }



    @PostMapping("/{id}/self")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public Review submitSelfReview(
            @PathVariable Long id,
            @RequestBody SubmitReviewRequest request
    ) {
        return service.submitSelfReview(id, request.comments);
    }

    /* ================= MANAGER ================= */

        @GetMapping("/team")
        @PreAuthorize("hasRole('MANAGER')")
        public List<ReviewResponse> getTeamReviews() {
            Long managerId = SecurityUtil.userId();
            return service.getTeamReviews(managerId);
        }






    @PostMapping("/{id}/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public Review submitManagerReview(
            @PathVariable Long id,
            @RequestBody SubmitReviewRequest request
    ) {
        return service.submitManagerReview(id, request.comments);
    }

    public ReviewResponse toResponse(Review review) {
        ReviewResponse r = new ReviewResponse();
        r.id = review.getId();
        r.employeeId = review.getEmployeeId();
        r.managerId = review.getManagerId();
        r.status = review.getStatus().name();
        r.selfReviewComments = review.getSelfReviewComments();
        r.managerReviewComments = review.getManagerReviewComments();
        r.reviewCycleId = review.getReviewCycle().getId();
        return r;
    }
}
