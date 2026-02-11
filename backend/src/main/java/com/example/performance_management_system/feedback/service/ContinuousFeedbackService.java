package com.example.performance_management_system.feedback.service;

import com.example.performance_management_system.common.error.ErrorCode;
import com.example.performance_management_system.common.exception.BusinessException;
import com.example.performance_management_system.config.security.SecurityUtil;
import com.example.performance_management_system.feedback.dto.CreateFeedbackRequest;
import com.example.performance_management_system.feedback.dto.FeedbackRecipientResponse;
import com.example.performance_management_system.feedback.dto.FeedbackResponse;
import com.example.performance_management_system.feedback.model.ContinuousFeedback;
import com.example.performance_management_system.feedback.model.FeedbackVisibility;
import com.example.performance_management_system.feedback.repository.ContinuousFeedbackRepository;
import com.example.performance_management_system.user.model.User;
import com.example.performance_management_system.user.repository.UserRepository;
import com.example.performance_management_system.user.service.HierarchyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContinuousFeedbackService {

    private final ContinuousFeedbackRepository feedbackRepository;
    private final HierarchyService hierarchyService;
    private final UserRepository userRepository;

    public ContinuousFeedbackService(
            ContinuousFeedbackRepository feedbackRepository,
            HierarchyService hierarchyService,
            UserRepository userRepository
    ) {
        this.feedbackRepository = feedbackRepository;
        this.hierarchyService = hierarchyService;
        this.userRepository = userRepository;
    }

    @Transactional
    public FeedbackResponse createFeedback(CreateFeedbackRequest request) {
        Long giverId = SecurityUtil.userId();

        if (giverId.equals(request.recipientId)) {
            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.INVALID_INPUT,
                    "You cannot send feedback to yourself"
            );
        }

        if (request.visibility == FeedbackVisibility.MANAGER_AND_EMPLOYEE
                && !hierarchyService.isManagerOf(giverId, request.recipientId)) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.ACCESS_DENIED,
                    "Only a direct manager can use MANAGER_AND_EMPLOYEE visibility"
            );
        }

        ContinuousFeedback feedback = new ContinuousFeedback();
        feedback.setGiverId(giverId);
        feedback.setRecipientId(request.recipientId);
        feedback.setType(request.type);
        feedback.setVisibility(request.visibility);
        feedback.setTitle(request.title.trim());
        feedback.setMessage(request.message.trim());
        feedback.setActionItems(request.actionItems == null ? null : request.actionItems.trim());

        return toDto(feedbackRepository.save(feedback));
    }

    public List<FeedbackRecipientResponse> getRecipients() {
        Long currentUserId = SecurityUtil.userId();
        String role = SecurityUtil.role();

        List<User> recipients = new ArrayList<>();

        if ("MANAGER".equalsIgnoreCase(role)) {
            recipients = userRepository.findByManagerId(currentUserId);
        } else if ("EMPLOYEE".equalsIgnoreCase(role)) {
            Long managerId = userRepository.findById(currentUserId)
                    .map(User::getManagerId)
                    .orElse(null);

            if (managerId != null) {
                userRepository.findById(managerId).ifPresent(recipients::add);
            }
        } else if ("HR".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            recipients = userRepository.findAll().stream().filter(User::getActive).toList();
        }

        return recipients.stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .map(this::toRecipientDto)
                .toList();
    }

    public Page<FeedbackResponse> getReceivedFeedback(int page, int size) {
        Long currentUserId = SecurityUtil.userId();
        return feedbackRepository
                .findByRecipientIdOrderByCreatedAtDesc(currentUserId, PageRequest.of(page, size))
                .map(this::toDto);
    }

    public Page<FeedbackResponse> getGivenFeedback(int page, int size) {
        Long currentUserId = SecurityUtil.userId();
        return feedbackRepository
                .findByGiverIdOrderByCreatedAtDesc(currentUserId, PageRequest.of(page, size))
                .map(this::toDto);
    }

    public Page<FeedbackResponse> getTeamFeedback(int page, int size) {
        Long managerId = SecurityUtil.userId();
        List<Long> reporteeIds = hierarchyService.getDirectReporteeIds(managerId);

        if (reporteeIds.isEmpty()) {
            return Page.empty();
        }

        return feedbackRepository
                .findByRecipientIdInOrderByCreatedAtDesc(reporteeIds, PageRequest.of(page, size))
                .map(this::toDto);
    }

    @Transactional
    public FeedbackResponse acknowledgeFeedback(Long feedbackId) {
        Long currentUserId = SecurityUtil.userId();

        ContinuousFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new BusinessException(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Feedback not found"
                ));

        if (!feedback.getRecipientId().equals(currentUserId)) {
            throw new BusinessException(
                    HttpStatus.FORBIDDEN,
                    ErrorCode.ACCESS_DENIED,
                    "Only the feedback recipient can acknowledge"
            );
        }

        feedback.setAcknowledged(Boolean.TRUE);
        feedback.setAcknowledgedAt(LocalDateTime.now());

        return toDto(feedbackRepository.save(feedback));
    }

    private FeedbackResponse toDto(ContinuousFeedback feedback) {
        FeedbackResponse dto = new FeedbackResponse();
        dto.id = feedback.getId();
        dto.giverId = feedback.getGiverId();
        dto.recipientId = feedback.getRecipientId();
        dto.type = feedback.getType();
        dto.visibility = feedback.getVisibility();
        dto.title = feedback.getTitle();
        dto.message = feedback.getMessage();
        dto.actionItems = feedback.getActionItems();
        dto.acknowledged = feedback.getAcknowledged();
        dto.acknowledgedAt = feedback.getAcknowledgedAt();
        dto.createdAt = feedback.getCreatedAt();
        return dto;
    }

    private FeedbackRecipientResponse toRecipientDto(User user) {
        FeedbackRecipientResponse dto = new FeedbackRecipientResponse();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.role = user.getRole().getName().name();
        return dto;
    }
}
