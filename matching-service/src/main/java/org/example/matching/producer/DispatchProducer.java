package org.example.matching.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.matching.entity.OutboxEvent;
import org.example.matching.repository.OutboxRepository;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.DispatchAssignedEvent;
import org.example.shared.events.HospitalAssignedEvent;
import org.springframework.stereotype.Service;

@Service
public class DispatchProducer {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper; // Spring Boot provides this automatically for converting to JSON

    public DispatchProducer(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public void publishDispatch(DispatchAssignedEvent event) {
        try {
            // 1. Convert the Java record into a JSON string
            String jsonPayload = objectMapper.writeValueAsString(event);

            // 2. Wrap it in our Outbox entity
            OutboxEvent outboxEvent = new OutboxEvent(
                    event.emergencyId().toString(),
                    KafkaTopics.DISPATCH_EVENTS,
                    jsonPayload
            );

            // 3. Save it to the database (The poller will pick this up later!)
            outboxRepository.save(outboxEvent);
            System.out.println("📦 OUTBOX SAVED: Dispatch for Emergency " + event.emergencyId());

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DispatchAssigned for outbox", e);
        }
    }

    public void publishHospitalAssigned(HospitalAssignedEvent event) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent(
                    event.emergencyId().toString(),
                    KafkaTopics.HOSPITAL_EVENTS,
                    jsonPayload
            );

            outboxRepository.save(outboxEvent);
            System.out.println("📦 OUTBOX SAVED: Hospital Assigned for Emergency " + event.emergencyId());

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize HospitalAssigned for outbox", e);
        }
    }
}