package com.example.performance_management_system.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {

    @NotBlank
    public String username;

    @NotBlank
    public String password;

    @NotNull
    public Long role;

    public Long managerId;
}
