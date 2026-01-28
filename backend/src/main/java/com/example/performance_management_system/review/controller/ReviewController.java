package com.example.performance_management_system.review.controller;

import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.review.dto.SubmitReviewRequest;
import com.example.performance_management_system.review.model.Review;
import com.example.performance_management_system.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping("/{id}/self-review")
    public Review submitSelfReview(@Valid @PathVariable Long id,
                                   @RequestBody SubmitReviewRequest req) {
        return service.submitSelfReview(id, req.comments);
    }

    @PostMapping("/{id}/manager-review")
    public Review submitManagerReview(@Valid @PathVariable Long id,
                                      @RequestBody SubmitReviewRequest req) {
        return service.submitManagerReview(id, req.comments);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public Page<Review> getMyTeamReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getReviewsForManager(
                SecurityUtil.userId(),
                page,
                size
        );
    }

}

