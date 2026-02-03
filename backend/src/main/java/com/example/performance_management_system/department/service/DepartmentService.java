package com.example.performance_management_system.department.service;

import com.example.performance_management_system.common.enums.DepartmentType;
import com.example.performance_management_system.department.dto.CreateDepartmentRequest;
import com.example.performance_management_system.department.model.Department;
import com.example.performance_management_system.department.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import jakarta.transaction.Transactional;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentService(DepartmentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Department getOrCreate(
            DepartmentType type,
            String displayName,
            Long headId
    ) {
        Department dept = repository.findByType(type)
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setType(type);
                    d.setActive(true);
                    return d;
                });

        // 🔥 ALWAYS update mutable fields
        dept.setDisplayName(
                displayName != null ? displayName : type.name()
        );
        dept.setHeadId(headId);

        // 🔥 ALWAYS save (create OR update)
        return repository.save(dept);
    }

    public List<Department> getAllDepartments() {
        return repository.findAll();
    }
}
