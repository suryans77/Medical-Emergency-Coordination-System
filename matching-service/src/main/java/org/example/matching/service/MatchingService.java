package org.example.matching.service;

import org.example.matching.producer.DispatchProducer;
import org.example.shared.dto.Ambulance;
import org.example.shared.events.DispatchAssigned;
import org.example.shared.events.EmergencyRequested;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MatchingService {

    private final DispatchProducer dispatchProducer;
    private final RestTemplate restTemplate;

    public MatchingService(DispatchProducer dispatchProducer) {
        this.dispatchProducer = dispatchProducer;
        this.restTemplate = new RestTemplate(); // Standard HTTP client for Phase 1
    }

    public void processEmergencyAndAssignAmbulance(EmergencyRequested event) {
        try {
            // 1. Fetch the first available ambulance via REST call
            // (Assuming ambulance-service runs on 8083)
            String ambulanceServiceUrl = "http://localhost:8083/ambulance/available";
            Ambulance availableAmbulance = restTemplate.getForObject(ambulanceServiceUrl, Ambulance.class);

            if (availableAmbulance != null) {
                // 2. Create the association event
                DispatchAssigned dispatchEvent = new DispatchAssigned(
                        event.emergencyId(),
                        availableAmbulance.ambulanceId(),
                        null
                );

                // 3. Broadcast the successful match
                dispatchProducer.publishDispatch(dispatchEvent);
            } else {
                System.err.println("CRITICAL: No available ambulances found for Emergency: " + event.emergencyId());
                // In Phase 2, this is where you would trigger a Saga compensation or retry logic
            }
        } catch (Exception e) {
            System.err.println("Failed to communicate with Ambulance Service: " + e.getMessage());
        }
    }
}
