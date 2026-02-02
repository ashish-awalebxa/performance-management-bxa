package com.example.performance_management_system.audit.consumer;

import com.example.performance_management_system.audit.model.AuditLog;
import com.example.performance_management_system.audit.repository.AuditLogRepository;
import com.example.performance_management_system.event.AuditEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditEventConsumer {

    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuditEventConsumer(AuditLogRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = {
                    "pms.auth.events",
                    "pms.user.events",
                    "pms.department.events",
                    "pms.goal.events",
                    "pms.review.events",
                    "pms.rating.events",
                    "pms.cycle.events"
            },
            containerFactory = "auditKafkaListenerFactory"
    )
    public void consume(AuditEvent event) {

        AuditLog log = new AuditLog();
        log.setEventId(event.eventId);
        log.setEventType(event.eventType.name());
        log.setDomain(event.domain.name());
        log.setAggregateId(event.aggregateId);

        if (event.actor != null) {
            log.setActorId(event.actor.id);
            log.setActorRole(event.actor.role);
        }

        try {
            log.setPayload(objectMapper.writeValueAsString(event.payload));
        } catch (Exception e) {
            log.setPayload("{}");
        }

        log.setCreatedAt(event.timestamp);

        repository.save(log);
    }
}
