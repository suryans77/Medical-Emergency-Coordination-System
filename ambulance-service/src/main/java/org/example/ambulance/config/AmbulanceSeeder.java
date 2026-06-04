package org.example.ambulance.config;

import jakarta.annotation.PostConstruct;
import org.example.ambulance.entity.Ambulance;
import org.example.ambulance.repository.AmbulanceRepository;
import org.example.shared.enums.AmbulanceStatus;
import org.springframework.stereotype.Component;

@Component
public class AmbulanceSeeder {

    private final AmbulanceRepository repository;

    public AmbulanceSeeder(AmbulanceRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seed() {
        if (repository.count() == 0) {
            repository.save(new Ambulance("DL-1AB-1234", "Advanced Life Support (ALS)", "2 Paramedics", AmbulanceStatus.AVAILABLE));
            repository.save(new Ambulance("DL-2CD-5678", "Basic Life Support (BLS)", "1 Paramedic", AmbulanceStatus.AVAILABLE));
            repository.save(new Ambulance("DL-3EF-9012", "Mobile ICU, Ventilator", "1 Doctor, 2 Paramedics", AmbulanceStatus.AVAILABLE));
            System.out.println("Seeded 3 ambulances with vehicle and crew data.");
        }
    }
}
