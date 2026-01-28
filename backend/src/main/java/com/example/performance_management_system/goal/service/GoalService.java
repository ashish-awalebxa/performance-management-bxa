package com.example.performance_management_system.goal.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.goal.dto.CreateGoalRequest;
import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.keyresult.model.KeyResult;
import com.example.performance_management_system.performancecycle.model.PerformanceCycle;
import com.example.performance_management_system.performancecycle.service.PerformanceCycleService;
import com.example.performance_management_system.goal.repository.GoalRepository;
import com.example.performance_management_system.user.service.HierarchyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final PerformanceCycleService cycleService;
    private final HierarchyService hierarchyService;

    public GoalService(GoalRepository goalRepository,
                       PerformanceCycleService cycleService,
                       HierarchyService hierarchyService) {
        this.goalRepository = goalRepository;
        this.cycleService = cycleService;
        this.hierarchyService = hierarchyService;
    }

    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    @Transactional
    public Goal createGoal(CreateGoalRequest req) {

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

        return goalRepository.save(goal);
    }

    @Transactional
    public Goal submitGoal(Long goalId) {
        Goal goal = findGoal(goalId);
        goal.submit();
        return goalRepository.save(goal);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @Transactional
    public Goal approveGoal(Long goalId) {

        Goal goal = findGoal(goalId);

        Long currentUserId = SecurityUtil.userId();
        String role = SecurityUtil.role();

        // HR / ADMIN bypass hierarchy
        if (!role.equals("HR") && !role.equals("ADMIN")) {
            hierarchyService.validateManagerAccess(
                    currentUserId,
                    goal.getEmployeeId()
            );
        }

        goal.approve();
        return goalRepository.save(goal);
    }


    private Goal findGoal(Long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Goal not found"));
    }


    public Page<Goal> getGoalsForEmployee(
            Long employeeId,
            int page,
            int size
    ) {
        return goalRepository.findByEmployeeId(
                employeeId,
                PageRequest.of(page, size)
        );
    }

}

