package org.example.case_.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.case_.entity.OutboxEvent;
import org.example.case_.repository.OutboxRepository;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.CaseCreated;
import org.springframework.stereotype.Service;

@Service
public class CaseProducer {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public CaseProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public void publishCaseCreated(CaseCreated event) {
        try {
            // 1. Serialize the event to a JSON string
            String jsonPayload = objectMapper.writeValueAsString(event);

            // 2. Wrap it in the Outbox entity
            OutboxEvent outboxEvent = new OutboxEvent(
                    event.emergencyId().toString(),
                    KafkaTopics.CASE_EVENTS,
                    jsonPayload
            );

            // 3. Save it to the database
            outboxRepository.save(outboxEvent);
            System.out.println("📦 OUTBOX SAVED: Case Created for Emergency ID: " + event.emergencyId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize CaseCreated for outbox", e);
        }
    }
}