package com.example.performance_management_system.audit.outbox;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface AuditOutboxRepository extends JpaRepository<AuditOutboxEvent, Long> {

    List<AuditOutboxEvent> findByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            Collection<OutboxStatus> statuses,
            Instant now,
            Pageable pageable
    );

    long countByStatusAndCreatedAtBefore(OutboxStatus status, Instant threshold);
}
