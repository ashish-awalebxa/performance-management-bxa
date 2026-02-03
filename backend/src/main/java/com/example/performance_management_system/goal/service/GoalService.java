package com.example.performance_management_system.goal.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.goal.dto.CreateGoalRequest;
import com.example.performance_management_system.goal.dto.GoalResponse;
import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.goal.repository.GoalRepository;
import com.example.performance_management_system.keyresult.dto.KeyResultResponse;
import com.example.performance_management_system.keyresult.model.KeyResult;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.performancecycle.service.PerformanceCycleService;
import com.example.performance_management_system.user.service.HierarchyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.List;

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

    /* ================= CREATE ================= */

    @Transactional
    public GoalResponse createGoal(CreateGoalRequest req) {

        PerformanceCycle activeCycle = cycleService.getActiveCycle();

        Goal goal = new Goal();
        goal.setTitle(req.title);
        goal.setDescription(req.description);
        goal.setEmployeeId(req.employeeId);
        goal.setPerformanceCycle(activeCycle);

        for (var krReq : req.keyResults) {
            KeyResult kr = new KeyResult();
            kr.setMetric(krReq.metric);
            kr.setTargetValue(krReq.targetValue);
            kr.setGoal(goal);
            goal.getKeyResults().add(kr);
        }

        return toGoalResponse(goalRepository.save(goal));
    }

    /* ================= EMPLOYEE ================= */

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

    @Transactional
    public GoalResponse submitGoal(Long goalId) {
        Goal goal = findGoal(goalId);
        goal.submit();
        return toGoalResponse(goalRepository.save(goal));
    }

    /* ================= MANAGER ================= */

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public GoalResponse approveGoal(Long goalId) {

        Goal goal = findGoal(goalId);

        hierarchyService.validateManagerAccess(
                SecurityUtil.userId(),
                goal.getEmployeeId()
        );

        goal.approve();
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

        goal.reject(reason);
        return toGoalResponse(goalRepository.save(goal));
    }

    @PreAuthorize("hasRole('MANAGER')")
    public Page<GoalResponse> getTeamGoals(int page, int size) {

        Long managerId = SecurityUtil.userId();
        PerformanceCycle activeCycle = cycleService.getActiveCycle();
        System.out.println(activeCycle.getName());

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

    /* ================= HELPERS ================= */

    private Goal findGoal(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Goal not found"));
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
}
