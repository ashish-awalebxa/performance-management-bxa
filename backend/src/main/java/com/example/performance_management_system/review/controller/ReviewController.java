package com.example.performance_management_system.review.controller;

import com.example.performance_management_system.review.dto.SubmitReviewRequest;
import com.example.performance_management_system.review.model.Review;
import com.example.performance_management_system.review.service.ReviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping("/{id}/self-review")
    public Review submitSelfReview(@PathVariable Long id,
                                   @RequestBody SubmitReviewRequest req) {
        return service.submitSelfReview(id, req.comments);
    }

    @PostMapping("/{id}/manager-review")
    public Review submitManagerReview(@PathVariable Long id,
                                      @RequestBody SubmitReviewRequest req) {
        return service.submitManagerReview(id, req.comments);
    }
}

