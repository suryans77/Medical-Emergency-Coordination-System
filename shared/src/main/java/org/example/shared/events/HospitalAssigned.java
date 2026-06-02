package org.example.shared.events;

import java.util.UUID;

public record HospitalAssigned(
        UUID emergencyId,
        UUID hospitalId,
        String hospitalName
) {}