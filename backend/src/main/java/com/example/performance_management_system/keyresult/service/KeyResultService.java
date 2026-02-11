package com.example.performance_management_system.keyresult.service;

import com.example.performance_management_system.common.error.ErrorCode;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.goal.model.Goal;
import com.example.performance_management_system.goal.model.GoalStatus;
import com.example.performance_management_system.goal.service.GoalService;
import com.example.performance_management_system.keyresult.model.KeyResult;
import com.example.performance_management_system.keyresult.repository.KeyResultRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeyResultService {

    private final KeyResultRepository repository;
    private final GoalService goalService;

    public KeyResultService(
            KeyResultRepository repository,
            GoalService goalService
    ) {
        this.repository = repository;
        this.goalService = goalService;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Transactional
    public KeyResult updateProgress(Long keyResultId, Double value) {

        KeyResult kr = repository.findById(keyResultId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SYSTEM_ERROR, // can be KEY_RESULT_NOT_FOUND later
                        "Key result not found"
                ));

        Goal goal = kr.getGoal();

        // ownership check
        if (!goal.getEmployeeId().equals(SecurityUtil.userId())) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.ACCESS_DENIED,
                    "You cannot update someone else's key result"
            );
        }

        if (goal.getStatus() != GoalStatus.APPROVED && goal.getStatus() != GoalStatus.COMPLETED) {
            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    ErrorCode.GOAL_INVALID_STATE,
                    "Progress can only be updated for approved goals"
            );
        }

        // business rule
        if (value > kr.getTargetValue()) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.VALIDATION_FAILED,
                    "Progress cannot exceed target value"
            );
        }

        try {
            kr.updateProgress(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.VALIDATION_FAILED,
                    e.getMessage()
            );
        }
        goalService.autoCompleteGoalIfEligible(goal);

        return repository.save(kr);
    }
}
