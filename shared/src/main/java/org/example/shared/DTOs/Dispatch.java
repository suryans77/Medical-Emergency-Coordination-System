package org.example.shared.DTOs;

import java.util.UUID;

public record Dispatch(
        UUID emergencyId,
        UUID ambulanceId
) {
}
