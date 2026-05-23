package org.example.shared.config;

public final class KafkaTopics {

    private KafkaTopics() {
        // Restrict instantiation
    }

    // Published by Emergency Service, consumed by Matching Service
    public static final String EMERGENCY_EVENTS = "emergency-events";

    // Published by Matching Service, consumed by Notification Service
    public static final String DISPATCH_EVENTS = "dispatch-events";
}