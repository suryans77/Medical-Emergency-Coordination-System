package org.example.notification.consumer;

import org.example.notification.service.NotificationService;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.CaseCreated;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DispatchConsumer {

    private final NotificationService notificationService;

    public DispatchConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = KafkaTopics.CASE_EVENTS, groupId = "notification-service-group")
    public void onCaseCreated(CaseCreated event) {
        try {
            notificationService.notify(
                    "Emergency Created: " + event.emergencyId(),
                    "Ambulance " + event.ambulanceId() + " Assigned. Hospital: " + event.hospitalName(),
                    "Case ID: " + event.caseId()
            );

        } catch (Exception e) {
            System.err.println("Failed to process notification: " + e.getMessage());
        }
    }
}
