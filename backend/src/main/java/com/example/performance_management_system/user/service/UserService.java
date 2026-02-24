package com.example.performance_management_system.user.service;

import com.example.performance_management_system.common.enums.DepartmentType;
import com.example.performance_management_system.common.enums.Role;
import com.example.performance_management_system.common.error.ErrorCode;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.department.model.Department;
import com.example.performance_management_system.department.service.DepartmentService;
import com.example.performance_management_system.event.Actor;
import com.example.performance_management_system.event.AuditEvent;
import com.example.performance_management_system.event.DomainType;
import com.example.performance_management_system.event.EventType;
import com.example.performance_management_system.event.producer.AuditEventProducer;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;
    private final AuditEventProducer auditEventProducer;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            DepartmentService departmentService,
            PasswordEncoder passwordEncoder,
            AuditEventProducer auditEventProducer
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentService = departmentService;
        this.passwordEncoder = passwordEncoder;
        this.auditEventProducer = auditEventProducer;
    }

    @Transactional
    public User createUser(@Valid CreateUserRequest request) {

        String currentRole = SecurityUtil.role();
        if (!currentRole.equals("ADMIN") && !currentRole.equals("HR")) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.ACCESS_DENIED,
                    "Only HR or ADMIN can create users"
            );
        }

        RoleEntity role = roleRepository.findByName(request.role)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SYSTEM_ERROR,
                        "Role not found"
                ));

        Department department = departmentService.getOrCreate(
                request.departmentType,
                request.departmentDisplayName,
                request.managerId
        );

        if (userRepository.findByEmail(request.email).isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.USER_ALREADY_EXISTS,
                    "Email already in use"
            );
        }

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
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND,
                        "User not found"
                ));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND,
                        "User not found"
                ));
    }

    public User getCurrentUser() {
        Long userId = SecurityUtil.userId();
        return getById(userId);
    }

    @Transactional
    public User updateCurrentUser(String name, String email) {

        Long userId = SecurityUtil.userId();

        User user = getById(userId);

        if (!user.getEmail().equals(email)
                && userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.USER_ALREADY_EXISTS,
                    "Email already in use"
            );
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

        User user = getById(userId);

        if (SecurityUtil.userId().equals(userId)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.VALIDATION_FAILED,
                    "Use /api/auth/me to update your own profile"
            );
        }

        if (!user.getEmail().equals(request.email)
                && userRepository.findByEmail(request.email).isPresent()) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.USER_ALREADY_EXISTS,
                    "Email already in use"
            );
        }

        RoleEntity role = roleRepository.findByName(request.role)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SYSTEM_ERROR,
                        "Role not found"
                ));

        Department department = departmentService.getOrCreate(
                request.departmentType,
                request.departmentDisplayName,
                request.managerId
        );

        String oldRole = user.getRole().getName().name();
        Long oldDepartmentId = user.getDepartment().getId();

        user.setName(request.name);
        user.setEmail(request.email);
        user.setRole(role);
        user.setDepartment(department);
        user.setManagerId(request.managerId);
        user.setActive(request.active);

        User updated = userRepository.save(user);

        AuditEvent event = AuditEvent.of(
                EventType.USER_UPDATED,
                DomainType.USER,
                updated.getId().toString(),
                new Actor(SecurityUtil.userId(), SecurityUtil.role()),
                Map.of(
                        "oldRole", oldRole,
                        "newRole", updated.getRole().getName().name(),
                        "oldDepartmentId", oldDepartmentId,
                        "newDepartmentId", updated.getDepartment().getId()
                )
        );

        auditEventProducer.publish(event);

        return updated;
    }

    public User registerUser(
            String name,
            String email,
            String password,
            Role role,
            DepartmentType departmentType,
            Long managerId
    ) {

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
