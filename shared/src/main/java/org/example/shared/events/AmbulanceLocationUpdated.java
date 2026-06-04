package org.example.shared.events;

import java.util.UUID;

public record AmbulanceLocationUpdated(
        UUID ambulanceId,
        double latitude,
        double longitude,
        String timestamp
) {}
