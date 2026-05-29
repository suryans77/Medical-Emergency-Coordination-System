package org.example.emergencyrequest.producer;

import org.example.shared.config.KafkaTopics;
import org.example.shared.events.EmergencyRequested;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmergencyRequestProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Constructor Injection (Spring automatically wires this)
    public EmergencyRequestProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEvent(EmergencyRequested event) {
        kafkaTemplate.send(KafkaTopics.EMERGENCY_EVENTS, event);
        System.out.println("Published EmergencyRequestedEvent to Kafka for ID: " + event.emergencyId());
    }
}
