package org.example.shared.events;

import org.example.shared.enums.CaseStatus;

import java.time.Instant;
import java.util.UUID;

public record CaseCreated(
        UUID eventId,
        Instant createdAt,
        UUID caseId,
        UUID emergencyId,
        UUID ambulanceId,
        UUID hospitalId,
        String hospitalName,
        CaseStatus status
) {}