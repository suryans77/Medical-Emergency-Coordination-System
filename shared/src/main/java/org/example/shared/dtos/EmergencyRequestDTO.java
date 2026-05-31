package org.example.shared.dtos;

import org.example.shared.enums.Severity;

public record EmergencyRequestDTO(
        String patientId,
        Severity severity,
        double latitude,
        double longitude
){}

