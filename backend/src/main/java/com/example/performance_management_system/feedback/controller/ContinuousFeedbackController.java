package com.example.performance_management_system.feedback.controller;

import com.example.performance_management_system.feedback.dto.CreateFeedbackRequest;
import com.example.performance_management_system.feedback.dto.FeedbackRecipientResponse;
import com.example.performance_management_system.feedback.dto.FeedbackResponse;
import com.example.performance_management_system.feedback.service.ContinuousFeedbackService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
public class ContinuousFeedbackController {

    private final ContinuousFeedbackService service;

    public ContinuousFeedbackController(ContinuousFeedbackService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public FeedbackResponse create(@Valid @RequestBody CreateFeedbackRequest request) {
        return service.createFeedback(request);
    }


    @GetMapping("/recipients")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
    public java.util.List<FeedbackRecipientResponse> getRecipients() {
        return service.getRecipients();
    }

    @GetMapping("/received")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public Page<FeedbackResponse> getReceived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getReceivedFeedback(page, size);
    }

    @GetMapping("/given")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public Page<FeedbackResponse> getGiven(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getGivenFeedback(page, size);
    }

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public Page<FeedbackResponse> getTeam(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getTeamFeedback(page, size);
    }

    @PatchMapping("/{id}/acknowledge")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public FeedbackResponse acknowledge(@PathVariable Long id) {
        return service.acknowledgeFeedback(id);
    }
}
