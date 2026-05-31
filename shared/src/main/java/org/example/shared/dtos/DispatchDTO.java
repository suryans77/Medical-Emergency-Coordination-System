package org.example.shared.dtos;

import java.util.UUID;

public record DispatchDTO(
        UUID emergencyId,
        UUID ambulanceId
) {
}
