package com.example.performance_management_system.goal.controller;

import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.goal.dto.CreateGoalRequest;
import com.example.performance_management_system.goal.dto.RejectGoalRequest;
import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.goal.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    @PostMapping
    public Goal create(@Valid @RequestBody CreateGoalRequest request) {
        return service.createGoal(request);
    }

    @PostMapping("/{id}/submit")
    public Goal submit(@PathVariable Long id) {
        return service.submitGoal(id);
    }

    @PostMapping("/{id}/approve")
    public Goal approve(@PathVariable Long id) {
        return service.approveGoal(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public Goal reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectGoalRequest request
    ) {
        return service.rejectGoal(id, request.reason);
    }


    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public Page<Goal> getMyGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = SecurityUtil.userId();
        return service.getGoalsForEmployee(userId, page, size);
    }

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public Page<Goal> getTeamGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getTeamGoals(page, size);
    }



}

