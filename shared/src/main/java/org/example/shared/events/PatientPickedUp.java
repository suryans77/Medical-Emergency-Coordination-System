package org.example.shared.events;

import java.time.Instant;
import java.util.UUID;

public record PatientPickedUp(
        UUID eventId,
        Instant createdAt,
        UUID caseId,
        UUID ambulanceId,
        Instant pickedUpAt
) {}