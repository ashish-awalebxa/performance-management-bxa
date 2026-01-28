package com.example.performance_management_system.reviewcycle.dto;


import java.time.LocalDate;

public class CreateReviewCycleRequest {

    public String name;
    public Boolean selfReviewEnabled;
    public Boolean managerReviewEnabled;
    public LocalDate startDate;
    public LocalDate endDate;
}
