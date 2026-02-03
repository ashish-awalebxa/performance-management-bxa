package com.example.performance_management_system.goal.controller;

import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.goal.dto.CreateGoalRequest;
import com.example.performance_management_system.goal.dto.GoalResponse;
import com.example.performance_management_system.goal.dto.RejectGoalRequest;
import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.goal.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService service;

    // ✅ Constructor injection (ONLY what controller needs)
    public GoalController(GoalService service) {
        this.service = service;
    }

    /* ================= EMPLOYEE / MANAGER ================= */

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public GoalResponse create(@Valid @RequestBody CreateGoalRequest request) {
        return service.createGoal(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('MANAGER')")
    public Page<GoalResponse> getMyGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = SecurityUtil.userId();
        return service.getGoalsForEmployee(userId, page, size);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public GoalResponse submit(@PathVariable Long id) {
        return service.submitGoal(id);
    }

    /* ================= MANAGER ================= */

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public Page<GoalResponse> getTeamGoals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getTeamGoals(page, size);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public GoalResponse approve(@PathVariable Long id) {
        return service.approveGoal(id);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public GoalResponse reject(
            @PathVariable Long id,
            @Valid @RequestBody RejectGoalRequest request
    ) {
        return service.rejectGoal(id, request.reason);
    }
}


