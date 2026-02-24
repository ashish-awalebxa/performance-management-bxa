package com.example.performance_management_system.event.producer;

import com.example.performance_management_system.audit.outbox.*;
import com.example.performance_management_system.event.AuditEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuditOutboxRelayService {

    private final AuditOutboxRepository outboxRepository;
    private final AuditDeadLetterRepository deadLetterRepository;
    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Counter publishedCounter;
    private final Counter deadLetterCounter;

    @Value("${audit.outbox.batch-size:50}")
    private int batchSize;

    @Value("${audit.outbox.max-retries:5}")
    private int maxRetries;

    @Value("${audit.outbox.retry-delay-seconds:30}")
    private long retryDelaySeconds;

    @Value("${audit.outbox.send-timeout-seconds:2}")
    private long sendTimeoutSeconds;

    @Value("${audit.outbox.stale-threshold-minutes:10}")
    private long staleThresholdMinutes;

    @Value("${audit.outbox.dlq-topic:pms.audit.events.dlq}")
    private String deadLetterTopic;

    public AuditOutboxRelayService(
            AuditOutboxRepository outboxRepository,
            AuditDeadLetterRepository deadLetterRepository,
            KafkaTemplate<String, AuditEvent> kafkaTemplate,
            ObjectMapper objectMapper,
            MeterRegistry meterRegistry
    ) {
        this.outboxRepository = outboxRepository;
        this.deadLetterRepository = deadLetterRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.publishedCounter = meterRegistry.counter("audit.outbox.published");
        this.deadLetterCounter = meterRegistry.counter("audit.outbox.dead_letter");
        Gauge.builder("audit.outbox.stale.pending", this::stalePendingCount)
                .description("Pending/failed outbox events older than threshold")
                .register(meterRegistry);
    }

    @PostConstruct
    void logOutboxConfig() {
        log.info("Audit outbox relay configured: batchSize={}, maxRetries={}, retryDelaySeconds={}",
                batchSize, maxRetries, retryDelaySeconds);
    }

    @Scheduled(fixedDelayString = "${audit.outbox.poll-interval-ms:5000}")
    @Transactional
    public void relayPendingEvents() {
        List<AuditOutboxEvent> events = outboxRepository
                .findByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                        Set.of(OutboxStatus.PENDING, OutboxStatus.FAILED),
                        Instant.now(),
                        PageRequest.of(0, batchSize)
                );

        for (AuditOutboxEvent event : events) {
            relay(event);
        }
    }

    private void relay(AuditOutboxEvent outboxEvent) {
        try {
            AuditEvent event = objectMapper.readValue(outboxEvent.getPayload(), AuditEvent.class);
            kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getMessageKey(), event)
                    .get(sendTimeoutSeconds, TimeUnit.SECONDS);

            outboxEvent.setStatus(OutboxStatus.SENT);
            outboxEvent.setSentAt(Instant.now());
            outboxEvent.setLastError(null);
            publishedCounter.increment();
        } catch (Exception ex) {
            handleFailure(outboxEvent, ex);
        }
    }

    private void handleFailure(AuditOutboxEvent event, Exception ex) {
        int attempts = event.getAttemptCount() + 1;
        event.setAttemptCount(attempts);

        String errorMessage = compactError(ex);
        event.setLastError(errorMessage);

        if (attempts >= maxRetries) {
            event.setStatus(OutboxStatus.DEAD_LETTER);
            deadLetterRepository.save(AuditDeadLetterEvent.fromOutbox(event, errorMessage));
            publishToDlq(event);
            deadLetterCounter.increment();
            log.error("Audit outbox moved to DLQ. eventId={}, attempts={}", event.getEventId(), attempts);
            return;
        }

        event.setStatus(OutboxStatus.FAILED);
        event.setNextAttemptAt(Instant.now().plusSeconds(retryDelaySeconds));
        log.warn("Audit outbox relay failed. eventId={}, attempt={}, nextRetryAt={}",
                event.getEventId(), attempts, event.getNextAttemptAt());
    }

    private void publishToDlq(AuditOutboxEvent event) {
        try {
            AuditEvent payload = objectMapper.readValue(event.getPayload(), AuditEvent.class);
            kafkaTemplate.send(deadLetterTopic, event.getMessageKey(), payload);
        } catch (Exception ex) {
            log.error("Failed to publish audit event to Kafka DLQ. eventId={}", event.getEventId(), ex);
        }
    }

    private String compactError(Exception ex) {
        String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        return msg.length() > 1800 ? msg.substring(0, 1800) : msg;
    }

    private double stalePendingCount() {
        Instant threshold = Instant.now().minus(Duration.ofMinutes(staleThresholdMinutes));
        return outboxRepository.countByStatusAndCreatedAtBefore(OutboxStatus.PENDING, threshold)
                + outboxRepository.countByStatusAndCreatedAtBefore(OutboxStatus.FAILED, threshold);
    }
}
