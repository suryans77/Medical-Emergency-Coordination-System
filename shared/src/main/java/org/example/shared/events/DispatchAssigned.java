package org.example.shared.events;

import java.time.Instant;
import java.util.UUID;

public record DispatchAssigned(
        UUID eventId,
        Instant createdAt,
        UUID emergencyId,
        UUID ambulanceId,
        UUID hospitalId
) {}