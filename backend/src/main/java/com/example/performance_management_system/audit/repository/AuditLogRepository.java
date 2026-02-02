package com.example.performance_management_system.audit.repository;

import com.example.performance_management_system.audit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
