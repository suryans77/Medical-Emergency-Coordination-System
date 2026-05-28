package org.example.shared.dtos;

import java.util.UUID;

public record Dispatch(
        UUID emergencyId,
        UUID ambulanceId
) {
}
