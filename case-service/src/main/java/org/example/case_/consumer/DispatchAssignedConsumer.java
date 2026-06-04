package org.example.case_.consumer;

import org.example.case_.service.CaseService;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.DispatchAssigned;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class DispatchAssignedConsumer {

    private final CaseService caseService;

    public DispatchAssignedConsumer(CaseService caseService) {
        this.caseService = caseService;
    }

    @KafkaListener(topics = KafkaTopics.DISPATCH_EVENTS, groupId = "case-service-group")
    public void consumeDispatch(DispatchAssigned event) {
        try {
            UUID emergencyId = event.emergencyId();
            System.out.println("🚑 Case Service received DispatchAssigned for " + emergencyId);
            caseService.onDispatchAssigned(event);
        } catch (Exception e) {
            System.err.println("Failed to parse Dispatch event: " + e.getMessage());
        }
    }
}