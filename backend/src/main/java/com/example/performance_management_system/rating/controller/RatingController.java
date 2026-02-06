package com.example.performance_management_system.rating.controller;

import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.rating.dto.CalibrateRatingRequest;
import com.example.performance_management_system.rating.dto.CreateRatingRequest;
import com.example.performance_management_system.rating.dto.UpdateManagerRatingRequest;
import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.rating.service.RatingService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/pending-calibration")
    @PreAuthorize("hasRole('HR')")
    public List<Rating> getRatingsForCalibration() {
        return service.getRatingsPendingCalibration();
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

    @GetMapping("/finalize")
    @PreAuthorize("hasRole('LEADERSHIP')")
    public List<Rating> getRatingsForFinalization() {
        return service.getRatingsForFinalization();
    }


    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public List<Rating> getTeamRatings() {
        Long managerId = SecurityUtil.userId();
        return service.getTeamRatings(managerId);
    }

    @GetMapping("/cycle/active")
    @PreAuthorize("hasRole('HR') or hasRole('LEADERSHIP')")
    public List<Rating> getActiveCycleRatings() {
        return service.getRatingsForActiveCycle();
    }

    @GetMapping
    @PreAuthorize("hasRole('HR') or hasRole('MANAGER') or hasRole('LEADERSHIP')")
    public Page<Rating> getRatingsForCycle(
            @RequestParam Long cycleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getRatingsForCycle(cycleId, page, size);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public Rating getMyRating() {
        return service.getMyFinalRating(SecurityUtil.userId());
    }

    @PutMapping("/{id}/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public Rating updateManagerRating(
            @PathVariable Long id,
            @RequestBody UpdateManagerRatingRequest req
    ) {
        return service.updateByManager(id, Double.valueOf(req.score), req.justification);
    }




}

