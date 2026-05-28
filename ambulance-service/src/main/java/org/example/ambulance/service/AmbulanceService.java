package org.example.ambulance.service;

import jakarta.annotation.PostConstruct;
import org.example.ambulance.entity.Ambulance;
import org.example.ambulance.repository.AmbulanceRepository;
import org.example.shared.enums.AmbulanceStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AmbulanceService {

    private final AmbulanceRepository repository;

    public AmbulanceService(AmbulanceRepository repository) {
        this.repository = repository;
    }

    // This method will be exposed via a Controller later for the Matching Service to call
    @Transactional(readOnly = true)
    public Optional<Ambulance> getAvailableAmbulance() {
        return repository.findFirstByStatus(AmbulanceStatus.AVAILABLE);
    }

    // Automatically runs once when the Spring Boot application boots up
    @PostConstruct
    public void seedInitialData() {
        if (repository.count() == 0) {
            Ambulance ambulanceA = new Ambulance();
            ambulanceA.setName("Ambulance A");
            ambulanceA.setLatitude(20.29);
            ambulanceA.setLongitude(85.81);
            ambulanceA.setStatus(AmbulanceStatus.AVAILABLE);

            Ambulance ambulanceB = new Ambulance();
            ambulanceB.setName("Ambulance B");
            ambulanceB.setLatitude(20.30); // Placed slightly north for testing
            ambulanceB.setLongitude(85.81);
            ambulanceB.setStatus(AmbulanceStatus.AVAILABLE);

            repository.save(ambulanceA);
            repository.save(ambulanceB);

            System.out.println("✅ Phase 1 Mock Data Seeded: Ambulance A & B are AVAILABLE.");
        }
    }
}