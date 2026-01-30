package com.example.performance_management_system.user.controller;


import com.example.performance_management_system.user.dto.CreateUserRequest;
import com.example.performance_management_system.user.dto.UpdateUserRequest;
import com.example.performance_management_system.user.dto.UserDetailResponse;
import com.example.performance_management_system.user.dto.UserListResponse;
import com.example.performance_management_system.user.model.User;
import com.example.performance_management_system.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public User create(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Page<UserListResponse> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getAllUsers(PageRequest.of(page, size))
                .map(user -> {
                    UserListResponse dto = new UserListResponse();
                    dto.id = user.getId();
                    dto.name = user.getName();
                    dto.email = user.getEmail();
                    dto.role = user.getRole().getName();
                    dto.departmentType = user.getDepartment().getType();
                    dto.departmentDisplayName = user.getDepartment().getDisplayName();
                    dto.managerId = user.getManagerId();
                    dto.active = user.getActive();
                    return dto;
                });
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public UserDetailResponse getUser(@PathVariable Long userId) {

        User user = userService.getById(userId);

        UserDetailResponse dto = new UserDetailResponse();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.role = user.getRole().getName();
        dto.departmentType = user.getDepartment().getType();
        dto.departmentDisplayName = user.getDepartment().getDisplayName();
        dto.managerId = user.getManagerId();
        dto.active = user.getActive();

        return dto;
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public UserDetailResponse updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        User user = userService.updateUser(userId, request);

        UserDetailResponse dto = new UserDetailResponse();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.role = user.getRole().getName();
        dto.departmentType = user.getDepartment().getType();
        dto.departmentDisplayName = user.getDepartment().getDisplayName();
        dto.managerId = user.getManagerId();
        dto.active = user.getActive();

        return dto;
    }





}
