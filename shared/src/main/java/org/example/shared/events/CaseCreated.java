package org.example.shared.events;

import org.example.shared.enums.CaseStatus;
import java.util.UUID;

public record CaseCreated(
        UUID caseId,
        UUID emergencyId,
        UUID ambulanceId,
        UUID hospitalId,
        String hospitalName,
        CaseStatus status
) {}