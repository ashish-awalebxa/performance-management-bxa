package com.example.performance_management_system.user.dto;

import com.example.performance_management_system.common.enums.DepartmentType;
import com.example.performance_management_system.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRequest {

    @NotBlank
    public String name;

    @Email
    @NotBlank
    public String email;

    @NotNull
    public Role role;

    @NotNull
    public DepartmentType departmentType;

    public String departmentDisplayName;

    public Long managerId;

    @NotNull
    public Boolean active;
}
