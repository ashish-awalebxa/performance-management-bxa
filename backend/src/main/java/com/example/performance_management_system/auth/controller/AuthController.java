package com.example.performance_management_system.auth.controller;

import com.example.performance_management_system.auth.dto.*;
import com.example.performance_management_system.auth.service.PasswordResetService;
import com.example.performance_management_system.config.security.jwt.JwtUtil;
import com.example.performance_management_system.user.model.User;
import com.example.performance_management_system.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;



    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                          PasswordResetService passwordResetService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {

        User user = userService.getByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),              // 👈 subject
                user.getRole().getName().name()
        );
    }

    @GetMapping("/me")
    public MeResponse me(){
        User user = userService.getCurrentUser();
        MeResponse response = new MeResponse();

        response.id = user.getId();
        response.name = user.getName();
        response.email = user.getEmail();
        response.role = user.getRole().getName();

        response.departmentType = user.getDepartment().getType();
        response.departmentDisplayName = user.getDepartment().getDisplayName();

        response.managerId = user.getManagerId();
        response.active = user.getActive();

        return response;

    }

    @PutMapping("/me")
    public MeResponse updateMe(@Valid @RequestBody UpdateMeRequest request){
        User user = userService.updateCurrentUser(request.name,
                request.email
        );

        MeResponse response = new MeResponse();
        response.id = user.getId();
        response.name = user.getName();
        response.email = user.getEmail();
        response.role = user.getRole().getName();
        response.departmentType = user.getDepartment().getType();
        response.departmentDisplayName = user.getDepartment().getDisplayName();
        response.managerId = user.getManagerId();
        response.active = user.getActive();

        return response;
    }


    @PostMapping("/logout")
    public void logout() {
        // Stateless logout:
        // Client is responsible for deleting the JWT.
        // This endpoint exists for API consistency & future extension.
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        // Token-only: return token directly
        return passwordResetService.createResetToken(request.email);
    }


    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(
                request.token,
                request.newPassword
        );
    }









}
