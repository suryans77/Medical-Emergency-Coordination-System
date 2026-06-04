package org.example.case_.consumer;

import org.example.case_.service.CaseService;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.HospitalAssigned;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HospitalAssignedConsumer {

    private final CaseService caseService;

    public HospitalAssignedConsumer(CaseService caseService) {
        this.caseService = caseService;
    }

    @KafkaListener(topics = KafkaTopics.HOSPITAL_EVENTS, groupId = "case-service-group")
    public void consumeHospital(HospitalAssigned event) {
        try {
            UUID emergencyId = event.emergencyId();
            System.out.println("🏥 Case Service received HospitalAssigned for " + emergencyId);
            caseService.onHospitalAssigned(event);
        } catch (Exception e) {
            System.err.println("Failed to parse Hospital event: " + e.getMessage());
        }
    }
}