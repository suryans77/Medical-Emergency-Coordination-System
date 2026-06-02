package org.example.shared.dto;

import org.example.shared.enums.CaseStatus;
import java.util.UUID;

public record CaseDTO(
        UUID caseId,
        UUID emergencyId,
        UUID ambulanceId,
        UUID hospitalId,
        CaseStatus status
) {}