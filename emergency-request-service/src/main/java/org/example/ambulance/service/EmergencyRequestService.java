package org.example.ambulance.service;

import org.example.shared.events.EmergencyRequested;
import org.example.ambulance.entity.EmergencyRequest;
import org.example.ambulance.producer.EmergencyRequestProducer;
import org.example.ambulance.repository.EmergencyRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmergencyRequestService {

    private final EmergencyRequestRepository repository;
    private final EmergencyRequestProducer producer;

    public EmergencyRequestService(EmergencyRequestRepository repository, EmergencyRequestProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    @Transactional
    public UUID processEmergency(org.example.shared.dtos.EmergencyRequest requestDTO) {
        EmergencyRequest entity = new EmergencyRequest();
        entity.setPatientId(requestDTO.patientId());
        entity.setSeverity(requestDTO.severity());
        entity.setLatitude(requestDTO.latitude());
        entity.setLongitude(requestDTO.longitude());
        entity.setStatus("PENDING_MATCH");

        EmergencyRequest savedEntity = repository.save(entity);

        EmergencyRequested event = new EmergencyRequested(
                savedEntity.getId(),
                requestDTO.patientId(),
                requestDTO.severity(),
                requestDTO.latitude(),
                requestDTO.longitude()
        );

        producer.publishEvent(event);

        return savedEntity.getId();
    }
}
