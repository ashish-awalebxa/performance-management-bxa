package com.example.performance_management_system.auth.controller;

import com.example.performance_management_system.config.security.jwt.JwtUtil;
import com.example.performance_management_system.user.model.User;
import com.example.performance_management_system.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@Valid @RequestParam String username,
                        @RequestParam String password) {

        User user = userService.getByUsername(username);

        // TEMP: plaintext check (hash later)
        if (!user.getPassword().equals(password)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().getName().name()
        );
    }
}
