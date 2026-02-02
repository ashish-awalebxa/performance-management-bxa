package com.example.performance_management_system.audit.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private String aggregateId;

    private Long actorId;
    private String actorRole;

    @Column(columnDefinition = "jsonb")
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;
}
