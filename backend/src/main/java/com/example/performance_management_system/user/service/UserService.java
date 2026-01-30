package com.example.performance_management_system.user.service;

import com.example.performance_management_system.common.enums.DepartmentType;
import com.example.performance_management_system.common.enums.Role;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.department.model.Department;
import com.example.performance_management_system.department.service.DepartmentService;
import com.example.performance_management_system.role.model.RoleEntity;
import com.example.performance_management_system.role.repository.RoleRepository;
import com.example.performance_management_system.user.dto.CreateUserRequest;
import com.example.performance_management_system.user.dto.UpdateUserRequest;
import com.example.performance_management_system.user.model.User;
import com.example.performance_management_system.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       DepartmentService departmentService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentService = departmentService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(@Valid CreateUserRequest request) {

        // RBAC SAFETY
        String currentRole = SecurityUtil.role();
        if (!currentRole.equals("ADMIN") && !currentRole.equals("HR")) {
            throw new BusinessException("Only HR or ADMIN can create users");
        }

        RoleEntity role = roleRepository.findByName(request.role)
                .orElseThrow(() -> new BusinessException("Role not found"));

        Department department = departmentService.getOrCreate(
                request.departmentType,
                request.departmentDisplayName,
                request.managerId
        );

        User user = new User();
        user.setName(request.username);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(role);
        user.setDepartment(department);
        user.setManagerId(request.managerId);
        user.setActive(true);

        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    public User getCurrentUser() {
        Long userId = SecurityUtil.userId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    @Transactional
    public User updateCurrentUser(String name, String email) {

        Long userId = SecurityUtil.userId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Optional: prevent duplicate email
        if (!user.getEmail().equals(email)
                && userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException("Email already in use");
        }

        user.setName(name);
        user.setEmail(email);

        return userRepository.save(user);
    }


    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }


    @Transactional
    public User updateUser(Long userId, UpdateUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Optional safety: prevent self-update via admin API
        if (SecurityUtil.userId().equals(userId)) {
            throw new BusinessException("Use /api/auth/me to update your own profile");
        }

        // Email uniqueness check
        if (!user.getEmail().equals(request.email)
                && userRepository.findByEmail(request.email).isPresent()) {
            throw new BusinessException("Email already in use");
        }

        RoleEntity role = roleRepository.findByName(request.role)
                .orElseThrow(() -> new BusinessException("Role not found"));

        Department department = departmentService.getOrCreate(
                request.departmentType,
                request.departmentDisplayName,
                request.managerId
        );

        user.setName(request.name);
        user.setEmail(request.email);
        user.setRole(role);
        user.setDepartment(department);
        user.setManagerId(request.managerId);
        user.setActive(request.active);

        return userRepository.save(user);
    }





















    public User registerUser(String name,
                             String email,
                             String password,
                             Role role,
                             DepartmentType departmentType,
                             Long managerId) {

        CreateUserRequest request = new CreateUserRequest();
        request.username = name;
        request.email = email;
        request.password = password;
        request.role = role;
        request.departmentType = departmentType;
        request.managerId = managerId;

        return createUser(request);
    }

}
