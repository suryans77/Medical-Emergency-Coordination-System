package org.example.case_.service;

import org.example.case_.entity.EmergencyCase;
import org.example.case_.producer.CaseProducer;
import org.example.case_.repository.CaseRepository;
import org.example.shared.enums.CaseStatus;
import org.example.shared.events.CaseCreated;
import org.example.shared.events.DispatchAssigned;
import org.example.shared.events.HospitalAssigned;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaseService {

    private final CaseRepository repository;
    private final CaseProducer producer;

    // Use strongly typed records instead of Maps
    private final Map<UUID, DispatchAssigned> pendingDispatches = new ConcurrentHashMap<>();
    private final Map<UUID, HospitalAssigned> pendingHospitals = new ConcurrentHashMap<>();

    public CaseService(CaseRepository repository, CaseProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    public void onDispatchAssigned(DispatchAssigned event) {
        pendingDispatches.put(event.emergencyId(), event);
        tryCreateCase(event.emergencyId());
    }

    public void onHospitalAssigned(HospitalAssigned event) {
        pendingHospitals.put(event.emergencyId(), event);
        tryCreateCase(event.emergencyId());
    }

    private synchronized void tryCreateCase(UUID emergencyId) {
        if (pendingDispatches.containsKey(emergencyId) && pendingHospitals.containsKey(emergencyId)) {

            DispatchAssigned dispatch = pendingDispatches.get(emergencyId);
            HospitalAssigned hospital = pendingHospitals.get(emergencyId);

            Instant now = Instant.now();
            EmergencyCase emergencyCase = new EmergencyCase(
                    emergencyId, dispatch.ambulanceId(), hospital.hospitalId(), CaseStatus.ASSIGNED, now, now
            );

            EmergencyCase savedCase = repository.save(emergencyCase);

            CaseCreated caseCreated = new CaseCreated(
                    savedCase.getCaseId(), emergencyId, dispatch.ambulanceId(), hospital.hospitalId(), hospital.hospitalName(), CaseStatus.ASSIGNED
            );
            producer.publishCaseCreated(caseCreated);

            pendingDispatches.remove(emergencyId);
            pendingHospitals.remove(emergencyId);

            System.out.println("✅ Official Emergency Case Saved! ID: " + savedCase.getCaseId());
        }
    }
}