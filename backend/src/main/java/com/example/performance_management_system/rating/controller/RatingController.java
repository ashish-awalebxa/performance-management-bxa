package com.example.performance_management_system.rating;

import com.example.performance_management_system.rating.dto.CalibrateRatingRequest;
import com.example.performance_management_system.rating.dto.CreateRatingRequest;
import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.rating.service.RatingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService service;

    public RatingController(RatingService service) {
        this.service = service;
    }

    @PostMapping
    public Rating create(@RequestBody CreateRatingRequest req) {
        return service.createRating(req);
    }

    @PostMapping("/{id}/submit")
    public Rating submit(@PathVariable Long id) {
        return service.submitByManager(id);
    }

    @PostMapping("/{id}/calibrate")
    public Rating calibrate(@PathVariable Long id,
                            @RequestBody CalibrateRatingRequest req) {
        return service.calibrate(id, req);
    }

    @PostMapping("/{id}/finalize")
    public Rating finalizeRating(@PathVariable Long id) {
        return service.finalizeRating(id);
    }
}

