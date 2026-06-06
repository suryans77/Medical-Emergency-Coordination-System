package org.example.shared.events;

import java.time.Instant;
import java.util.UUID;

public record PatientDelivered(
        UUID eventId,
        Instant createdAt,
        UUID caseId,
        UUID hospitalId,
        Instant deliveredAt
) {}