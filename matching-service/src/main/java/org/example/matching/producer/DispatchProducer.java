package org.example.matching.producer;

import org.example.shared.config.KafkaTopics;
import org.example.shared.events.DispatchAssigned;
import org.example.shared.events.HospitalAssigned;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DispatchProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DispatchProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishDispatch(DispatchAssigned event) {
        kafkaTemplate.send(KafkaTopics.DISPATCH_EVENTS, event.emergencyId().toString(), event);
        System.out.println("🚀 DISPATCH PUBLISHED for Emergency: " + event.emergencyId());
    }

    public void publishHospitalAssigned(HospitalAssigned event) {
        kafkaTemplate.send(KafkaTopics.HOSPITAL_EVENTS, event.emergencyId().toString(), event);
        System.out.println("🏥 HOSPITAL ASSIGNED PUBLISHED for Emergency: " + event.emergencyId());
    }
}