package org.example.case_.entity;

import jakarta.persistence.*;
import org.example.shared.enums.CaseStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "emergency_cases")
public class EmergencyCase {

    @Id
    @GeneratedValue
    private UUID caseId;

    private UUID emergencyId;
    private UUID ambulanceId;
    private UUID hospitalId;

    @Enumerated(EnumType.STRING)
    private CaseStatus status;

    private Instant createdAt;
    private Instant updatedAt; // Crucial for Phase 3 state tracking

    public EmergencyCase() {}

    public EmergencyCase(UUID emergencyId, UUID ambulanceId, UUID hospitalId, CaseStatus status, Instant createdAt, Instant updatedAt) {
        this.emergencyId = emergencyId;
        this.ambulanceId = ambulanceId;
        this.hospitalId = hospitalId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public UUID getEmergencyId() { return emergencyId; }
    public void setEmergencyId(UUID emergencyId) { this.emergencyId = emergencyId; }
    public UUID getAmbulanceId() { return ambulanceId; }
    public void setAmbulanceId(UUID ambulanceId) { this.ambulanceId = ambulanceId; }
    public UUID getHospitalId() { return hospitalId; }
    public void setHospitalId(UUID hospitalId) { this.hospitalId = hospitalId; }
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}