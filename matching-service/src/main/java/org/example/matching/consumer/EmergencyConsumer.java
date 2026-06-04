package org.example.matching.consumer;

import org.example.matching.service.MatchingService;
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

    @KafkaListener(topics = "emergency-events", groupId = "matching-service-group")
    public void onEmergencyRequested(Map<String, Object> eventPayload) {
        try {
            System.out.println("🚨 Received new emergency request!");

            UUID emergencyId = UUID.fromString((String) eventPayload.get("id"));
            double latitude = (Double) eventPayload.get("latitude");
            double longitude = (Double) eventPayload.get("longitude");

            // Kick off the matching algorithm
            matchingService.processEmergency(emergencyId, latitude, longitude);

        } catch (Exception e) {
            System.err.println("⚠️ Failed to parse incoming emergency event: " + e.getMessage());
        }
    }
}