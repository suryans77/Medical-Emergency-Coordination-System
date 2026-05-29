package org.example.matching.producer;

import org.example.shared.config.KafkaTopics;
import org.example.shared.events.DispatchAssigned;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DispatchProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DispatchProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDispatch(DispatchAssigned event) {
        kafkaTemplate.send(KafkaTopics.DISPATCH_EVENTS, event);
        System.out.println("Published DispatchAssignedEvent for Emergency: " + event.emergencyId() + " to Ambulance: " + event.ambulanceId());
    }
}
