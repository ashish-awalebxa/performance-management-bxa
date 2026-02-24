package com.example.performance_management_system.event.producer;

import com.example.performance_management_system.event.AuditEvent;
import com.example.performance_management_system.event.AuditTopicResolver;
import com.example.performance_management_system.event.DomainType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AuditEventProducer {

    private static final Logger log =
            LoggerFactory.getLogger(AuditEventProducer.class);

    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    public AuditEventProducer(KafkaTemplate<String, AuditEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AuditEvent event) {
        try {
            String topic = AuditTopicResolver.resolve(event.domain);
            kafkaTemplate.send(topic, event.aggregateId, event);
        } catch (Exception ex) {
            // 🔴 DO NOT FAIL BUSINESS FLOW
            log.error("Failed to publish audit event {}", event.eventType, ex);
        }
    }
}
