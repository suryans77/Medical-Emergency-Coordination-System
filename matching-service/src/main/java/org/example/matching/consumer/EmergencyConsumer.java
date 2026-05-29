package org.example.matching.consumer;

import org.example.matching.service.MatchingService;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.EmergencyRequested;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmergencyConsumer {

    private final MatchingService matchingService;

    public EmergencyConsumer(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @KafkaListener(topics = KafkaTopics.EMERGENCY_EVENTS, groupId = "matching-service-group")
    public void consumeEmergencyRequest(EmergencyRequested event) {
        System.out.println("Received Emergency Request for ID: " + event.emergencyId());

        // Pass the event to the business logic layer
        matchingService.processEmergencyAndAssignAmbulance(event);
    }
}
