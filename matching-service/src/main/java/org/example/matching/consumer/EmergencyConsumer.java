package org.example.matching.consumer;

import org.example.matching.service.MatchingService;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.EmergencyRequested;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class EmergencyConsumer {

    private final MatchingService matchingService;

    public EmergencyConsumer(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @KafkaListener(topics = KafkaTopics.EMERGENCY_EVENTS, groupId = "matching-service-group")
    public void onEmergencyRequested(EmergencyRequested event) {
        try {
            System.out.println("🚨 Received new emergency request!");
            UUID emergencyId = event.emergencyId();
            double latitude = event.latitude();
            double longitude = event.longitude();

            matchingService.processEmergency(emergencyId, latitude, longitude);

        } catch (Exception e) {
            System.err.println("⚠️ Failed to parse incoming emergency event: " + e.getMessage());
        }
    }
}