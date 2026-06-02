package org.example.ambulance.service;

import org.example.ambulance.entity.Ambulance;
import org.example.ambulance.repository.AmbulanceRepository;
import org.example.shared.enums.AmbulanceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AmbulanceService {

    private final AmbulanceRepository repository;

    public AmbulanceService(AmbulanceRepository repository) {
        this.repository = repository;
    }

    public List<Ambulance> getByStatus(AmbulanceStatus status) {
        return repository.findByStatus(status);
    }

    @Transactional
    public void updateStatus(UUID id, AmbulanceStatus newStatus) {
        Ambulance ambulance = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ambulance not found: " + id));
        ambulance.setStatus(newStatus);
        repository.save(ambulance);
    }
}