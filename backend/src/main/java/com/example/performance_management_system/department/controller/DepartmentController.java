package com.example.performance_management_system.department.controller;


import com.example.performance_management_system.department.dto.CreateDepartmentRequest;
import com.example.performance_management_system.department.model.Department;
import com.example.performance_management_system.department.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@PreAuthorize("hasRole('HR') or hasRole('ADMIN')")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @PostMapping
    public Department create(@Valid @RequestBody CreateDepartmentRequest request) {
        return service.getOrCreate(
                request.type,
                request.displayName,
                request.headId
        );
    }

    @GetMapping
    public List<Department> list() {
        return service.getAllDepartments();
    }

}
