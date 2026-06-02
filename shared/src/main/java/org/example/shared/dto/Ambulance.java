package org.example.shared.dto;

import org.example.shared.enums.AmbulanceStatus;

import java.util.UUID;

public record Ambulance(
        UUID ambulanceId,
        double latitude,
        double longitude,
        AmbulanceStatus status
) {}

