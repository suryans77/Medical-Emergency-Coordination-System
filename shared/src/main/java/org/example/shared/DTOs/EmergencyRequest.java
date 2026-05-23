package org.example.shared.DTOs;

import org.example.shared.enums.Severity;

public record EmergencyRequest(
        String patientId;

        Severity severity;

        double latitude;

        double longitude;
){}

