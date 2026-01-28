package com.example.performance_management_system.goal.dto;

import com.example.performance_management_system.keyresult.dto.KeyResultRequest;

import java.util.List;

public class CreateGoalRequest {

    public String title;
    public String description;
    public Long employeeId;
    public List<KeyResultRequest> keyResults;
}
