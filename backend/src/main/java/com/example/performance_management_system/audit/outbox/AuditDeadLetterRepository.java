package com.example.performance_management_system.audit.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditDeadLetterRepository extends JpaRepository<AuditDeadLetterEvent, Long> {
}
