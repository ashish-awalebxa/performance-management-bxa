package com.example.performance_management_system.rating.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.performancecycle.model.CycleStatus;
import com.example.performance_management_system.performancecycle.service.PerformanceCycleService;
import com.example.performance_management_system.rating.dto.CalibrateRatingRequest;
import com.example.performance_management_system.rating.dto.CreateRatingRequest;
import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.rating.model.RatingStatus;
import com.example.performance_management_system.rating.repository.RatingRepository;
import com.example.performance_management_system.user.service.HierarchyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository repository;
    private final PerformanceCycleService cycleService;
    private final HierarchyService hierarchyService;

    public RatingService(RatingRepository repository,
                         PerformanceCycleService cycleService,
                         HierarchyService hierarchyService) {
        this.repository = repository;
        this.cycleService = cycleService;
        this.hierarchyService = hierarchyService;
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('HR')")
    @Transactional
    public Rating createRating(CreateRatingRequest req) {

        var cycle = cycleService.getActiveCycle();

        if (repository.findByEmployeeIdAndPerformanceCycle(
                req.employeeId, cycle
        ).isPresent()) {
            throw new BusinessException(
                    "Rating already exists for this employee in this cycle"
            );
        }

        Rating rating = new Rating();
        rating.setEmployeeId(req.employeeId);
        rating.setPerformanceCycle(cycle);

        // 🔥 THIS IS THE KEY LINE
        Long managerId = hierarchyService.getManagerId(req.employeeId);
        rating.setManagerId(managerId);

        rating.setScore(req.score);
        rating.setManagerJustification(req.managerJustification);

        return repository.save(rating);
    }


    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public Rating submitByManager(Long ratingId) {

        Rating rating = get(ratingId);

        if (rating.getStatus() != RatingStatus.DRAFT) {
            throw new BusinessException("Rating is not in draft state");
        }


        Long managerId = SecurityUtil.userId();
        String role = SecurityUtil.role();

        if (!role.equals("HR") && !role.equals("ADMIN")) {
            hierarchyService.validateManagerAccess(
                    managerId,
                    rating.getEmployeeId()
            );
        }

        rating.submitByManager();
        return repository.save(rating);
    }


    @PreAuthorize("hasRole('HR')")
    @Transactional
    public Rating calibrate(Long ratingId, CalibrateRatingRequest req) {
        Rating rating = get(ratingId);
        if (rating.getStatus() != RatingStatus.MANAGER_SUBMITTED) {
            throw new BusinessException("Rating must be manager submitted before calibration");
        }

        rating.calibrateByHr(req.newScore, req.justification);
        return repository.save(rating);
    }

    @PreAuthorize("hasRole('LEADERSHIP')")
    @Transactional
    public Rating finalizeRating(Long ratingId) {
        Rating rating = get(ratingId);
        if (rating.getStatus() != RatingStatus.HR_CALIBRATED) {
            throw new BusinessException("Rating must be calibrated before finalization");
        }

        rating.finalizeRating();
        return repository.save(rating);
    }

    private Rating get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Rating not found"));
    }

    public Page<Rating> getRatingsForCycle(
            Long cycleId,
            int page,
            int size
    ) {
        return repository.findByPerformanceCycle_Id(
                cycleId,
                PageRequest.of(page, size)
        );
    }

    @PreAuthorize("hasRole('MANAGER')")
    public List<Rating> getTeamRatings(Long managerId) {

        var cycle = cycleService.getActiveCycle();

        return repository.findByManagerIdAndPerformanceCycle(
                managerId,
                cycle
        );
    }

    @PreAuthorize("hasRole('HR')")
    public List<Rating> getRatingsPendingCalibration() {

        var cycle = cycleService.getActiveCycle();

        return repository.findByStatusAndPerformanceCycle(
                RatingStatus.MANAGER_SUBMITTED,
                cycle
        );
    }

    @PreAuthorize("hasRole('LEADERSHIP')")
    public List<Rating> getRatingsForFinalization() {

        var cycle = cycleService.getActiveCycle();

        return repository.findByStatusAndPerformanceCycle(
                RatingStatus.HR_CALIBRATED,
                cycle
        );
    }

    public List<Rating> getRatingsForActiveCycle() {
        var cycle = cycleService.getActiveCycle();
        return repository.findByPerformanceCycle(cycle);
    }

    public Rating getMyActiveRating(Long employeeId) {

        return repository
                .findByEmployeeIdAndPerformanceCycle_Status(
                        employeeId,
                        CycleStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new BusinessException("Rating not yet available"));
    }

    public Rating getMyFinalRating(Long employeeId) {
        return repository.findByEmployeeIdAndStatus(
                employeeId,
                RatingStatus.FINALIZED
        ).orElseThrow(() ->
                new BusinessException("Rating not finalized yet")
        );
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public Rating updateByManager(
            Long ratingId,
            Double score,
            String justification
    ) {
        Rating rating = get(ratingId);

        if (!rating.getManagerId().equals(SecurityUtil.userId())) {
            throw new BusinessException("Unauthorized");
        }

        if (rating.getStatus() != RatingStatus.DRAFT) {
            throw new BusinessException("Rating cannot be edited now");
        }

        rating.setScore(score);
        rating.setManagerJustification(justification);

        return repository.save(rating);
    }



}

