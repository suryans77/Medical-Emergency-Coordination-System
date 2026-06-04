package org.example.case_.producer;

import org.example.shared.config.KafkaTopics;
import org.example.shared.events.CaseCreated;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CaseProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CaseProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCaseCreated(CaseCreated event) {
        kafkaTemplate.send(KafkaTopics.CASE_EVENTS, event.caseId().toString(), event);
        System.out.println("📁 CASE CREATED Published: " + event.caseId());
    }
}