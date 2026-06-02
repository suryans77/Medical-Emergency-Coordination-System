package org.example.shared.enums;

public enum AmbulanceStatus {
    AVAILABLE,
    RESERVED,     // matched, waiting to depart
    IN_TRANSIT,   // currently carrying patient
    OFFLINE       // out of service
}