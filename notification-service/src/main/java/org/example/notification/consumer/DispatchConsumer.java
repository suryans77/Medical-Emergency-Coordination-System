package org.example.notification.consumer;

import org.example.notification.service.NotificationService;
import org.example.shared.config.KafkaTopics;
import org.example.shared.events.DispatchAssigned;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DispatchConsumer {

    private final NotificationService notificationService;

    public DispatchConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = KafkaTopics.DISPATCH_EVENTS, groupId = "notification-service-group")
    public void consumeDispatchEvent(DispatchAssigned event) {
        notificationService.notifyPatient(event);
    }
}
