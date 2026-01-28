package com.example.performance_management_system.goal.controller;

import com.example.performance_management_system.goal.dto.CreateGoalRequest;
import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.goal.service.GoalService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    @PostMapping
    public Goal create(@RequestBody CreateGoalRequest request) {
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
}

