package org.example.matching.scheduler;

import org.example.matching.entity.OutboxEvent;
import org.example.matching.repository.OutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate; // Note: We are sending JSON Strings now!

    public OutboxPublisher(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 1000) // Runs every 5 seconds
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByStatus("PENDING");

        for (OutboxEvent event : pendingEvents) {
            try {
                // Send to Kafka using the Topic Name we stored in 'eventType'
                kafkaTemplate.send(event.getEventType(), event.getPayload()).get(); // .get() forces it to wait for success

                // If successful, mark as PUBLISHED
                event.setStatus("PUBLISHED");
                event.setPublishedAt(Instant.now());
                outboxRepository.save(event);

                System.out.println("📤 Kafka Published [" + event.getEventType() + "] for Emergency ID: " + event.getAggregateId());
            } catch (Exception e) {
                System.err.println("⚠️ Outbox failed to publish for Emergency ID: " + event.getAggregateId());
            }

        }
    }
}