package com.example.performance_management_system.department.controller;


import com.example.performance_management_system.department.dto.CreateDepartmentRequest;
import com.example.performance_management_system.department.model.Department;
import com.example.performance_management_system.department.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


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

    @PutMapping("/{id}")
    public Department update(
            @PathVariable Long id,
            @Valid @RequestBody CreateDepartmentRequest request
    ) {
        return service.updateDepartment(id, request.displayName, request.headId);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        service.deactivateDepartment(id);
    }

    @GetMapping
    public Page<Department> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return service.getAllDepartments(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

}
