package com.example.performance_management_system.department.service;

import com.example.performance_management_system.common.enums.DepartmentType;
import com.example.performance_management_system.common.error.ErrorCode;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.department.model.Department;
import com.example.performance_management_system.department.repository.DepartmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


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
        dept.setActive(true);

        // 🔥 ALWAYS save (create OR update)
        return repository.save(dept);
    }

    @Transactional
    public Department updateDepartment(Long id, String displayName, Long headId) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SYSTEM_ERROR,
                        "Department not found"
                ));

        department.setDisplayName(displayName != null ? displayName : department.getType().name());
        department.setHeadId(headId);

        return repository.save(department);
    }

    @Transactional
    public void deactivateDepartment(Long id) {
        Department department = repository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SYSTEM_ERROR,
                        "Department not found"
                ));

        department.setActive(false);
        repository.save(department);
    }

    public Page<Department> getAllDepartments(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
