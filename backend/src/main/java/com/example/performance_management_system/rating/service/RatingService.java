package com.example.performance_management_system.rating.service;

import com.example.performance_management_system.common.error.ErrorCode;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.goal.model.GoalStatus;
import com.example.performance_management_system.goal.repository.GoalRepository;
import com.example.performance_management_system.keyresult.model.KeyResult;
import com.example.performance_management_system.performancecycle.model.CycleStatus;
import com.example.performance_management_system.performancecycle.service.PerformanceCycleService;
import com.example.performance_management_system.rating.dto.CalibrateRatingRequest;
import com.example.performance_management_system.rating.dto.CreateRatingRequest;
import com.example.performance_management_system.rating.model.Rating;
import com.example.performance_management_system.rating.model.RatingStatus;
import com.example.performance_management_system.rating.repository.RatingRepository;
import com.example.performance_management_system.user.repository.UserRepository;
import com.example.performance_management_system.user.service.HierarchyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository repository;
    private final PerformanceCycleService cycleService;
    private final HierarchyService hierarchyService;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;

    public RatingService(
            RatingRepository repository,
            PerformanceCycleService cycleService,
            HierarchyService hierarchyService,
            UserRepository userRepository,
            GoalRepository goalRepository
    ) {
        this.repository = repository;
        this.cycleService = cycleService;
        this.hierarchyService = hierarchyService;
        this.userRepository = userRepository;
        this.goalRepository = goalRepository;
    }

    /* ================= CREATE ================= */

    @PreAuthorize("hasRole('MANAGER') or hasRole('HR')")
    @Transactional
    public Rating createRating(CreateRatingRequest req) {

        var cycle = cycleService.getActiveCycle();

        if (repository.findByEmployeeIdAndPerformanceCycle(
                req.employeeId, cycle
        ).isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.RATING_ALREADY_EXISTS,
                    "Rating already exists for this employee in this cycle"
            );
        }

        Rating rating = new Rating();
        rating.setEmployeeId(req.employeeId);
        rating.setPerformanceCycle(cycle);

        // 🔥 enforce hierarchy consistency
        Long managerId = hierarchyService.getManagerId(req.employeeId);

        if ("MANAGER".equals(SecurityUtil.role())) {
            hierarchyService.validateManagerAccess(SecurityUtil.userId(), req.employeeId);
        }

        rating.setManagerId(managerId);

        rating.setScore(calculateScoreFromGoals(req.employeeId));
        rating.setManagerJustification(req.managerJustification);

        return repository.save(rating);
    }

    /* ================= MANAGER ================= */

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public Rating submitByManager(Long ratingId) {

        Rating rating = get(ratingId);

        if (rating.getStatus() != RatingStatus.DRAFT) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.RATING_INVALID_STATE,
                    "Rating is not in draft state"
            );
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

    /* ================= HR ================= */

    @PreAuthorize("hasRole('HR')")
    @Transactional
    public Rating calibrate(Long ratingId, CalibrateRatingRequest req) {

        Rating rating = get(ratingId);

        if (rating.getStatus() != RatingStatus.MANAGER_SUBMITTED) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.RATING_INVALID_STATE,
                    "Rating must be manager submitted before calibration"
            );
        }

        rating.calibrateByHr(req.newScore, req.justification);
        return repository.save(rating);
    }

    /* ================= LEADERSHIP ================= */

    @PreAuthorize("hasRole('LEADERSHIP')")
    @Transactional
    public Rating finalizeRating(Long ratingId) {

        Rating rating = get(ratingId);

        if (rating.getStatus() != RatingStatus.HR_CALIBRATED) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.RATING_INVALID_STATE,
                    "Rating must be calibrated before finalization"
            );
        }

        rating.finalizeRating();
        return repository.save(rating);
    }

    /* ================= READ ================= */

    private Rating get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SYSTEM_ERROR, // can be RATING_NOT_FOUND later
                        "Rating not found"
                ));
    }

    public Page<Rating> getRatingsForCycle(
            Long cycleId,
            int page,
            int size
    ) {
        Page<Rating> ratingsPage;

        if ("MANAGER".equals(SecurityUtil.role())) {
            ratingsPage = repository.findByManagerIdAndPerformanceCycle_Id(
                    SecurityUtil.userId(),
                    cycleId,
                    PageRequest.of(page, size)
            );
        } else {
            ratingsPage = repository.findByPerformanceCycle_Id(
                    cycleId,
                    PageRequest.of(page, size)
            );
        }

        var enrichedRatings = enrichRatingsWithEmployeeNames(ratingsPage.getContent());

        return new PageImpl<>(
                enrichedRatings,
                ratingsPage.getPageable(),
                ratingsPage.getTotalElements()
        );
    }

    @PreAuthorize("hasRole('MANAGER')")
    public List<Rating> getTeamRatings(Long managerId) {

        var cycle = cycleService.getActiveCycle();

        List<Rating> ratings = repository.findByManagerIdAndPerformanceCycle(
                managerId,
                cycle
        );
        return enrichRatingsWithEmployeeNames(ratings);
    }

    @PreAuthorize("hasRole('HR')")
    public List<Rating> getRatingsPendingCalibration() {

        var cycle = cycleService.getActiveCycle();

        List<Rating> ratings = repository.findByStatusAndPerformanceCycle(
                RatingStatus.MANAGER_SUBMITTED,
                cycle
        );
        return enrichRatingsWithEmployeeNames(ratings);
    }

    @PreAuthorize("hasRole('LEADERSHIP')")
    public List<Rating> getRatingsForFinalization() {

        var cycle = cycleService.getActiveCycle();

        List<Rating> ratings = repository.findByStatusAndPerformanceCycle(
                RatingStatus.HR_CALIBRATED,
                cycle
        );
        return enrichRatingsWithEmployeeNames(ratings);
    }

    public List<Rating> getRatingsForActiveCycle() {
        var cycle = cycleService.getActiveCycle();

        List<Rating> ratings;
        String role = SecurityUtil.role();

        if ("HR".equals(role)) {
            ratings = repository.findByStatusAndPerformanceCycle(RatingStatus.MANAGER_SUBMITTED, cycle);
        } else if ("LEADERSHIP".equals(role)) {
            ratings = repository.findByStatusAndPerformanceCycle(RatingStatus.HR_CALIBRATED, cycle);
        } else {
            ratings = repository.findByPerformanceCycle(cycle);
        }

        return enrichRatingsWithEmployeeNames(ratings);
    }

    public Rating getMyActiveRating(Long employeeId) {

        return repository
                .findByEmployeeIdAndPerformanceCycle_Status(
                        employeeId,
                        CycleStatus.ACTIVE
                )
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.RATING_INVALID_STATE,
                        "Rating not yet available"
                ));
    }

    public Rating getMyFinalRating(Long employeeId) {

        return repository.findByEmployeeIdAndStatus(
                employeeId,
                RatingStatus.FINALIZED
        ).orElseThrow(() -> new BusinessException(
                HttpStatus.NOT_FOUND,
                ErrorCode.RATING_INVALID_STATE,
                "Rating not finalized yet"
        ));
    }


    private List<Rating> enrichRatingsWithEmployeeNames(List<Rating> ratings) {
        if (ratings.isEmpty()) {
            return ratings;
        }

        var employeeIds = ratings.stream()
                .map(Rating::getEmployeeId)
                .distinct()
                .toList();

        var employeeById = userRepository.findAllById(employeeIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        user -> user.getId(),
                        user -> user.getName()
                ));

        ratings.forEach(rating -> rating.setEmployeeName(
                employeeById.getOrDefault(rating.getEmployeeId(), "Unknown")
        ));

        return ratings;
    }


    private Double calculateScoreFromGoals(Long employeeId) {
        var activeCycle = cycleService.getActiveCycle();

        List<Goal> goals = goalRepository.findByEmployeeIdAndPerformanceCycle_Id(
                employeeId,
                activeCycle.getId()
        );

        if (goals.isEmpty()) {
            return 1.0;
        }

        double averageScore = goals.stream()
                .mapToDouble(this::calculateGoalScore)
                .average()
                .orElse(1.0);

        double boundedScore = Math.max(1.0, Math.min(5.0, averageScore));
        return Math.round(boundedScore * 100.0) / 100.0;
    }

    private double calculateGoalScore(Goal goal) {
        double progress = goal.getKeyResults().stream()
                .mapToDouble(this::calculateKeyResultProgress)
                .average()
                .orElse(0.0);

        double statusWeight = mapGoalStatusWeight(goal.getStatus());

        return progress * statusWeight * 5.0;
    }

    private double calculateKeyResultProgress(KeyResult keyResult) {
        if (keyResult.getTargetValue() == null || keyResult.getTargetValue() <= 0) {
            return 0.0;
        }

        double current = keyResult.getCurrentValue() == null ? 0.0 : keyResult.getCurrentValue();
        return Math.max(0.0, Math.min(1.0, current / keyResult.getTargetValue()));
    }

    private double mapGoalStatusWeight(GoalStatus status) {
        return switch (status) {
            case COMPLETED -> 1.0;
            case APPROVED -> 0.9;
            case SUBMITTED -> 0.75;
            case DRAFT -> 0.5;
            case REJECTED -> 0.2;
            case ARCHIVED -> 0.0;
        };
    }

    /* ================= UPDATE ================= */

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public Rating updateByManager(
            Long ratingId,
            Double score,
            String justification
    ) {

        Rating rating = get(ratingId);

        if (!rating.getManagerId().equals(SecurityUtil.userId())) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.ACCESS_DENIED,
                    "Unauthorized"
            );
        }

        if (rating.getStatus() != RatingStatus.DRAFT) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.RATING_INVALID_STATE,
                    "Rating cannot be edited now"
            );
        }

        rating.setScore(calculateScoreFromGoals(rating.getEmployeeId()));
        rating.setManagerJustification(justification);

        return repository.save(rating);
    }
}
