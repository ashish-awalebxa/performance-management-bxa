package com.example.performance_management_system.rating.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.performancecycle.service.PerformanceCycleService;
import com.example.performance_management_system.rating.dto.CalibrateRatingRequest;
import com.example.performance_management_system.rating.dto.CreateRatingRequest;
import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.rating.repository.RatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {

    private final RatingRepository repository;
    private final PerformanceCycleService cycleService;

    public RatingService(RatingRepository repository,
                         PerformanceCycleService cycleService) {
        this.repository = repository;
        this.cycleService = cycleService;
    }

    @Transactional
    public Rating createRating(CreateRatingRequest req) {

        var cycle = cycleService.getActiveCycle();

        if (repository.findByEmployeeIdAndPerformanceCycle(req.employeeId, cycle).isPresent()) {
            throw new BusinessException("Rating already exists for this employee in this cycle");
        }

        Rating rating = new Rating();
        rating.setEmployeeId(req.employeeId);
        rating.setPerformanceCycle(cycle);
        rating.setScore(req.score);
        rating.setManagerJustification(req.managerJustification);

        return repository.save(rating);
    }

    @Transactional
    public Rating submitByManager(Long ratingId) {
        Rating rating = get(ratingId);
        rating.submitByManager();
        return repository.save(rating);
    }

    @Transactional
    public Rating calibrate(Long ratingId, CalibrateRatingRequest req) {
        Rating rating = get(ratingId);
        rating.calibrateByHr(req.newScore, req.justification);
        return repository.save(rating);
    }

    @Transactional
    public Rating finalizeRating(Long ratingId) {
        Rating rating = get(ratingId);
        rating.finalizeRating();
        return repository.save(rating);
    }

    private Rating get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Rating not found"));
    }
}

