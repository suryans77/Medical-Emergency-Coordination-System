package org.example.shared.events;

import java.time.Instant;
import java.util.UUID;

public record AmbulanceLocationUpdated(
        UUID ambulanceId,
        double latitude,
        double longitude,
        Instant timestamp
) {}