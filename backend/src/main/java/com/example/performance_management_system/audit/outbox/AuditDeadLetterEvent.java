package com.example.performance_management_system.audit.outbox;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "audit_dead_letter")
public class AuditDeadLetterEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String topic;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(length = 2000)
    private String errorMessage;

    @Column(nullable = false)
    private Instant failedAt;

    public static AuditDeadLetterEvent fromOutbox(AuditOutboxEvent outbox, String errorMessage) {
        AuditDeadLetterEvent deadLetterEvent = new AuditDeadLetterEvent();
        deadLetterEvent.eventId = outbox.getEventId();
        deadLetterEvent.topic = outbox.getTopic();
        deadLetterEvent.payload = outbox.getPayload();
        deadLetterEvent.errorMessage = errorMessage;
        deadLetterEvent.failedAt = Instant.now();
        return deadLetterEvent;
    }
}
