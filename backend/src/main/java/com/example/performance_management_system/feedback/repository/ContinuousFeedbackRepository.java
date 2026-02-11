package com.example.performance_management_system.feedback.repository;

import com.example.performance_management_system.feedback.model.ContinuousFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContinuousFeedbackRepository extends JpaRepository<ContinuousFeedback, Long> {

    Page<ContinuousFeedback> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    Page<ContinuousFeedback> findByGiverIdOrderByCreatedAtDesc(Long giverId, Pageable pageable);

    Page<ContinuousFeedback> findByRecipientIdInOrderByCreatedAtDesc(List<Long> recipientIds, Pageable pageable);
}
