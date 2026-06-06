package org.example.emergencyrequest.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.emergencyrequest.entity.OutboxEvent;
import org.example.emergencyrequest.repository.OutboxRepository;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.EmergencyRequested;
import org.springframework.stereotype.Component;

@Component
public class EmergencyRequestProducer {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    // Constructor Injection
    public EmergencyRequestProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public void publishEvent(EmergencyRequested event) {
        try {
            // 1. Translate the Java event to a JSON string
            String jsonPayload = objectMapper.writeValueAsString(event);

            // 2. Package it into an Outbox DB entity
            OutboxEvent outboxEvent = new OutboxEvent(
                    event.emergencyId().toString(),
                    KafkaTopics.EMERGENCY_EVENTS,
                    jsonPayload
            );

            // 3. Save it to the database!
            // (The poller will wake up in a few seconds and actually send it to Kafka)
            outboxRepository.save(outboxEvent);
            System.out.println("📦 Saved Emergency Request to OUTBOX for ID: " + event.emergencyId());

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize EmergencyRequested for outbox", e);
        }
    }
}