package com.example.performance_management_system.goal.dto;

import com.example.performance_management_system.goal.model.GoalStatus;
import com.example.performance_management_system.keyresult.dto.KeyResultResponse;

import java.util.List;

public class GoalResponse {

    public Long id;
    public String title;
    public String description;
    public GoalStatus status;

    public Long employeeId;

    public String cycleName;
    public String cycleType;

    public List<KeyResultResponse> keyResults;
}
