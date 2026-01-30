package com.example.performance_management_system.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordRequest {

    @NotBlank
    public String token;

    @NotBlank
    public String newPassword;
}
