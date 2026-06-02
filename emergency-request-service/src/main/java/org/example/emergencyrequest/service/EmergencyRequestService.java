package org.example.emergencyrequest.service;

import org.example.emergencyrequest.entity.EmergencyRequest;
import org.example.emergencyrequest.producer.EmergencyRequestProducer;
import org.example.emergencyrequest.repository.EmergencyRequestRepository;
import org.example.shared.dto.EmergencyRequestDTO;
import org.example.shared.events.EmergencyRequested;
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
    public UUID processEmergency(EmergencyRequestDTO requestDTO) {
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
