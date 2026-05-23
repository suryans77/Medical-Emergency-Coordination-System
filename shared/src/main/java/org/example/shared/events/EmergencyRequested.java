package org.example.shared.events;

import org.example.shared.enums.Severity;

import java.util.UUID;

public class EmergencyRequested {
    UUID emergencyId;

    String patientId;

    Severity severity;

    double latitude;

    double longitude;
}
