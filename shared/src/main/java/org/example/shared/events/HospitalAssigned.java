package org.example.shared.events;

import java.time.Instant;
import java.util.UUID;

public record HospitalAssigned(
        UUID eventId,
        Instant createdAt,
        UUID emergencyId,
        UUID hospitalId,
        String hospitalName
) {}