package org.example.shared.events;

import java.time.Instant;
import java.util.UUID;

public record PatientPickedUp(
        UUID caseId,
        UUID ambulanceId,
        Instant pickedUpAt
) {}