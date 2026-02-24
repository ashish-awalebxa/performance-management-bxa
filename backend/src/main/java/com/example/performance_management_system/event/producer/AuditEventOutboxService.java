package com.example.performance_management_system.event.producer;

import com.example.performance_management_system.audit.outbox.AuditOutboxEvent;
import com.example.performance_management_system.audit.outbox.AuditOutboxRepository;
import com.example.performance_management_system.event.AuditEvent;
import com.example.performance_management_system.event.AuditTopicResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class AuditEventOutboxService {

    private final AuditOutboxRepository auditOutboxRepository;
    private final ObjectMapper objectMapper;

    public AuditEventOutboxService(
            AuditOutboxRepository auditOutboxRepository,
            ObjectMapper objectMapper
    ) {
        this.auditOutboxRepository = auditOutboxRepository;
        this.objectMapper = objectMapper;
    }

    public void enqueue(AuditEvent event) {
        String topic = AuditTopicResolver.resolve(event.domain);
        String payload = serialize(event);

        AuditOutboxEvent outbox = AuditOutboxEvent.pending(
                topic,
                event.aggregateId,
                event,
                payload
        );

        auditOutboxRepository.save(outbox);
    }

    private String serialize(AuditEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize audit event " + event.eventId, ex);
        }
    }
}
