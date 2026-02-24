package com.example.performance_management_system.audit.outbox;

import com.example.performance_management_system.event.AuditEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "audit_outbox")
public class AuditOutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String messageKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private Integer attemptCount = 0;

    @Column(nullable = false)
    private Instant nextAttemptAt;

    private Instant sentAt;

    @Column(length = 2000)
    private String lastError;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;

    public static AuditOutboxEvent pending(String topic, String messageKey, AuditEvent event, String payload) {
        AuditOutboxEvent outboxEvent = new AuditOutboxEvent();
        outboxEvent.eventId = event.eventId;
        outboxEvent.topic = topic;
        outboxEvent.messageKey = messageKey;
        outboxEvent.status = OutboxStatus.PENDING;
        outboxEvent.attemptCount = 0;
        outboxEvent.nextAttemptAt = Instant.now();
        outboxEvent.createdAt = Instant.now();
        outboxEvent.payload = payload;
        return outboxEvent;
    }
}
