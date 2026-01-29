package com.example.performance_management_system.user.dto;

import com.example.performance_management_system.common.enums.Role;
import com.example.performance_management_system.common.enums.DepartmentType;

public class UserDetailResponse {

    public Long id;
    public String name;
    public String email;
    public Role role;

    public DepartmentType departmentType;
    public String departmentDisplayName;

    public Long managerId;
    public Boolean active;
}
