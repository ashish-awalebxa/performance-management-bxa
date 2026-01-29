package com.example.performance_management_system.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateMeRequest {

    @NotBlank
    public String name;

    @Email
    @NotBlank
    public String email;
}
