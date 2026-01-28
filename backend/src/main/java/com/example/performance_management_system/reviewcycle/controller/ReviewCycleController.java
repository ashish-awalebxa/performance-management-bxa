package com.example.performance_management_system.reviewcycle.controller;

import com.example.performance_management_system.reviewcycle.dto.CreateReviewCycleRequest;
import com.example.performance_management_system.reviewcycle.model.ReviewCycle;
import com.example.performance_management_system.reviewcycle.service.ReviewCycleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review-cycles")
public class ReviewCycleController {

    private final ReviewCycleService service;

    public ReviewCycleController(ReviewCycleService service) {
        this.service = service;
    }

    @PostMapping
    public ReviewCycle create(@RequestBody CreateReviewCycleRequest req) {
        ReviewCycle cycle = new ReviewCycle();
        cycle.setName(req.name);
        cycle.setSelfReviewEnabled(req.selfReviewEnabled);
        cycle.setManagerReviewEnabled(req.managerReviewEnabled);
        cycle.setStartDate(req.startDate);
        cycle.setEndDate(req.endDate);

        return service.create(cycle);
    }

    @PostMapping("/{id}/activate")
    public ReviewCycle activate(@PathVariable Long id) {
        return service.activate(id);
    }

    @PostMapping("/{id}/close")
    public ReviewCycle close(@PathVariable Long id) {
        return service.close(id);
    }
}

