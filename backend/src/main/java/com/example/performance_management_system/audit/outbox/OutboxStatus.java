package com.example.performance_management_system.audit.outbox;

public enum OutboxStatus {
    PENDING,
    FAILED,
    SENT,
    DEAD_LETTER
}
