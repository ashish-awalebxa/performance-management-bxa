package com.example.performance_management_system.goal.service;

import com.example.performance_management_system.common.error.ErrorCode;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.goal.dto.CreateGoalRequest;
import com.example.performance_management_system.goal.dto.GoalResponse;
import com.example.performance_management_system.goal.dto.ManagerDashboardSummary;
import com.example.performance_management_system.goal.dto.UpdateGoalRequest;
import com.example.performance_management_system.goal.dto.UpsertKeyResultRequest;
import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.goal.model.GoalStatus;
import com.example.performance_management_system.goal.repository.GoalRepository;
import com.example.performance_management_system.keyresult.dto.KeyResultResponse;
import com.example.performance_management_system.keyresult.model.KeyResult;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.performancecycle.service.PerformanceCycleService;
import com.example.performance_management_system.user.service.HierarchyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final PerformanceCycleService cycleService;
    private final HierarchyService hierarchyService;

    public GoalService(
            GoalRepository goalRepository,
            PerformanceCycleService cycleService,
            HierarchyService hierarchyService
    ) {
        this.goalRepository = goalRepository;
        this.cycleService = cycleService;
        this.hierarchyService = hierarchyService;
    }

    @Transactional
    public GoalResponse createGoal(CreateGoalRequest req) {
        Long actorId = SecurityUtil.userId();
        String actorRole = SecurityUtil.role();
        validateCreateAccess(actorId, actorRole, req.employeeId);

        PerformanceCycle activeCycle = cycleService.getActiveCycle();

        Goal goal = new Goal();
        goal.setTitle(req.title);
        goal.setDescription(req.description);
        goal.setEmployeeId(req.employeeId);
        goal.setPerformanceCycle(activeCycle);

        req.keyResults.forEach((krReq) -> {
            KeyResult kr = new KeyResult();
            kr.setMetric(krReq.metric);
            kr.setTargetValue(krReq.targetValue);
            kr.setGoal(goal);
            goal.getKeyResults().add(kr);
        });

        return toGoalResponse(goalRepository.save(goal));
    }

    public Page<GoalResponse> getGoalsForEmployee(
            Long employeeId,
            int page,
            int size
    ) {
        return goalRepository.findByEmployeeId(
                employeeId,
                PageRequest.of(page, size)
        ).map(this::toGoalResponse);
    }

    public GoalResponse getGoalById(Long goalId) {
        Goal goal = findGoal(goalId);
        validateReadAccess(goal);
        return toGoalResponse(goal);
    }

    @Transactional
    public GoalResponse updateGoal(Long goalId, UpdateGoalRequest request) {
        Goal goal = findGoal(goalId);
        validateEmployeeOwnership(goal);
        validateEditableStatus(goal);

        goal.setTitle(request.title);
        goal.setDescription(request.description);

        Map<Long, KeyResult> existingById = new LinkedHashMap<>();
        goal.getKeyResults().forEach((kr) -> existingById.put(kr.getId(), kr));

        goal.getKeyResults().clear();

        request.keyResults.forEach((krRequest) -> {
            KeyResult keyResult = resolveOrCreateKeyResult(krRequest, existingById, goal);
            keyResult.setMetric(krRequest.metric);
            keyResult.setTargetValue(krRequest.targetValue);

            if (keyResult.getCurrentValue() > keyResult.getTargetValue()) {
                keyResult.setCurrentValue(keyResult.getTargetValue());
            }

            goal.getKeyResults().add(keyResult);
        });

        return toGoalResponse(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        Goal goal = findGoal(goalId);
        validateEmployeeOwnership(goal);
        validateEditableStatus(goal);
        goalRepository.delete(goal);
    }

    @Transactional
    public GoalResponse submitGoal(Long goalId) {
        Goal goal = findGoal(goalId);
        validateEmployeeOwnership(goal);

        try {
            goal.submit();
        } catch (IllegalStateException e) {
            throw goalStateException(e.getMessage());
        }

        return toGoalResponse(goalRepository.save(goal));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public GoalResponse approveGoal(Long goalId) {
        Goal goal = findGoal(goalId);

        hierarchyService.validateManagerAccess(
                SecurityUtil.userId(),
                goal.getEmployeeId()
        );

        try {
            goal.approve();
        } catch (IllegalStateException e) {
            throw goalStateException(e.getMessage());
        }

        autoCompleteGoalIfEligible(goal);

        return toGoalResponse(goalRepository.save(goal));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public GoalResponse rejectGoal(Long goalId, String reason) {
        Goal goal = findGoal(goalId);

        hierarchyService.validateManagerAccess(
                SecurityUtil.userId(),
                goal.getEmployeeId()
        );

        try {
            goal.reject(reason);
        } catch (IllegalStateException e) {
            throw goalStateException(e.getMessage());
        }

        return toGoalResponse(goalRepository.save(goal));
    }

    @PreAuthorize("hasRole('MANAGER')")
    public Page<GoalResponse> getTeamGoals(int page, int size) {

        Long managerId = SecurityUtil.userId();
        PerformanceCycle activeCycle = cycleService.getActiveCycle();

        List<Long> reporteeIds =
                hierarchyService.getDirectReporteeIds(managerId);

        if (reporteeIds.isEmpty()) {
            return Page.empty();
        }

        return goalRepository
                .findByEmployeeIdInAndPerformanceCycle_Id(
                        reporteeIds,
                        activeCycle.getId(),
                        PageRequest.of(page, size)
                )
                .map(this::toGoalResponse);
    }

    private Goal findGoal(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.GOAL_NOT_FOUND,
                        "Goal not found"
                ));
    }

    private GoalResponse toGoalResponse(Goal goal) {

        GoalResponse dto = new GoalResponse();

        dto.id = goal.getId();
        dto.title = goal.getTitle();
        dto.description = goal.getDescription();
        dto.status = goal.getStatus();
        dto.employeeId = goal.getEmployeeId();

        dto.cycleName = goal.getPerformanceCycle().getName();
        dto.cycleType = goal.getPerformanceCycle().getCycleType();

        dto.keyResults = goal.getKeyResults()
                .stream()
                .map(this::toKeyResultResponse)
                .toList();

        return dto;
    }

    private KeyResultResponse toKeyResultResponse(KeyResult kr) {

        KeyResultResponse dto = new KeyResultResponse();
        dto.id = kr.getId();
        dto.metric = kr.getMetric();
        dto.targetValue = kr.getTargetValue();
        dto.currentValue = kr.getCurrentValue();
        return dto;
    }

    @PreAuthorize("hasRole('MANAGER')")
    public ManagerDashboardSummary getManagerDashboardSummary() {

        Long managerId = SecurityUtil.userId();

        List<Long> reporteeIds =
                hierarchyService.getDirectReporteeIds(managerId);

        if (reporteeIds.isEmpty()) {
            return new ManagerDashboardSummary();
        }

        PerformanceCycle activeCycle = cycleService.getActiveCycle();

        List<Goal> goals = goalRepository
                .findByEmployeeIdInAndPerformanceCycle_Id(
                        reporteeIds,
                        activeCycle.getId()
                );

        ManagerDashboardSummary dto = new ManagerDashboardSummary();
        dto.cycleName = activeCycle.getName();
        dto.totalGoals = goals.size();
        dto.pendingApprovals = goals.stream()
                .filter(g -> g.getStatus() == GoalStatus.SUBMITTED)
                .count();
        dto.completedGoals = goals.stream()
                .filter(g -> g.getStatus() == GoalStatus.COMPLETED)
                .count();

        return dto;
    }

    public void autoCompleteGoalIfEligible(Goal goal) {

        boolean allDone = goal.getKeyResults().stream()
                .allMatch(kr -> kr.getCurrentValue() >= kr.getTargetValue());

        if (allDone) {
            goal.setStatus(GoalStatus.COMPLETED);
        }
    }

    private void validateCreateAccess(Long actorId, String actorRole, Long requestedEmployeeId) {
        if ("MANAGER".equals(actorRole)) {
            if (!actorId.equals(requestedEmployeeId)) {
                hierarchyService.validateManagerAccess(actorId, requestedEmployeeId);
            }
            return;
        }

        if (!actorId.equals(requestedEmployeeId)) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.ACCESS_DENIED,
                    "Employees can only create goals for themselves"
            );
        }
    }

    private void validateReadAccess(Goal goal) {
        Long actorId = SecurityUtil.userId();
        String actorRole = SecurityUtil.role();

        if (actorId.equals(goal.getEmployeeId())) {
            return;
        }

        if ("MANAGER".equals(actorRole)) {
            hierarchyService.validateManagerAccess(actorId, goal.getEmployeeId());
            return;
        }

        throw new BusinessException(
                HttpStatus.FORBIDDEN,
                ErrorCode.ACCESS_DENIED,
                "You are not allowed to access this goal"
        );
    }

    private void validateEmployeeOwnership(Goal goal) {
        if (!SecurityUtil.userId().equals(goal.getEmployeeId())) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.ACCESS_DENIED,
                    "You can only modify your own goals"
            );
        }
    }

    private void validateEditableStatus(Goal goal) {
        if (goal.getStatus() != GoalStatus.DRAFT && goal.getStatus() != GoalStatus.REJECTED) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.GOAL_INVALID_STATE,
                    "Only DRAFT or REJECTED goals can be edited or deleted"
            );
        }
    }

    private KeyResult resolveOrCreateKeyResult(
            UpsertKeyResultRequest request,
            Map<Long, KeyResult> existingById,
            Goal goal
    ) {
        if (request.id == null) {
            KeyResult fresh = new KeyResult();
            fresh.setGoal(goal);
            return fresh;
        }

        KeyResult existing = existingById.remove(request.id);
        if (existing == null) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.VALIDATION_FAILED,
                    "Key result id " + request.id + " does not belong to this goal"
            );
        }

        existing.setGoal(goal);
        return existing;
    }

    private BusinessException goalStateException(String message) {
        return new BusinessException(
                HttpStatus.CONFLICT,
                ErrorCode.GOAL_INVALID_STATE,
                message
        );
    }
}
