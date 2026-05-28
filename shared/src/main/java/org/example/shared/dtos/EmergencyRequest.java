package org.example.shared.dtos;

import org.example.shared.enums.Severity;

public record EmergencyRequest(
        String patientId,
        Severity severity,
        double latitude,
        double longitude
){}

