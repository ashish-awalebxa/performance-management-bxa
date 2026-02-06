package com.example.performance_management_system.user.service;

import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.user.model.User;
import org.springframework.stereotype.Service;
import com.example.performance_management_system.user.repository.UserRepository;

import java.util.List;

@Service
public class HierarchyService {

    private final UserService userService;
    private final UserRepository userRepository;

    public HierarchyService(UserRepository userRepository,
                            UserService userService) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Checks if managerId is the direct manager of employeeId
     */
    public boolean isManagerOf(Long managerId, Long employeeId) {

        if (managerId.equals(employeeId)) {
            return false;
        }

        User employee = userService.getById(employeeId);

        return employee.getManagerId() != null
                && employee.getManagerId().equals(managerId);
    }

    /**
     * Throws exception if manager is not allowed to act on employee
     */
    public void validateManagerAccess(Long managerId, Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("Employee not found"));

        if (!managerId.equals(employee.getManagerId())) {
            throw new BusinessException("You are not the manager of this employee");
        }
    }

    public List<Long> getDirectReporteeIds(Long managerId) {
        return userRepository.findByManagerId(managerId)
                .stream()
                .map(User::getId)
                .toList();
    }

    public Long getManagerId(Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException("Employee not found"));

        if (employee.getManagerId() == null) {
            throw new BusinessException("Employee has no assigned manager");
        }

        return employee.getManagerId();
    }

}
