package com.example.performance_management_system.performance.domain.model.entity;

import com.example.performance_management_system.performance.domain.model.valueobject.EmployeeId;

import java.util.Objects;

public final class Employee {
    private final EmployeeId employeeId;
    private final String fullName;

    public Employee(EmployeeId employeeId, String fullName) {
        this.employeeId = Objects.requireNonNull(employeeId, "Employee id is required");
        this.fullName = Objects.requireNonNull(fullName, "Employee full name is required");
    }

    public EmployeeId employeeId() {
        return employeeId;
    }

    public String fullName() {
        return fullName;
    }
}
