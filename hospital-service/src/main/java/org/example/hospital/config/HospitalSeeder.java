package org.example.hospital.config;

import jakarta.annotation.PostConstruct;
import org.example.hospital.entity.Hospital;
import org.example.hospital.repository.HospitalRepository;
import org.springframework.stereotype.Component;

@Component
public class HospitalSeeder {

    private final HospitalRepository repository;

    public HospitalSeeder(HospitalRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seed() {
        if (repository.count() == 0) {
            repository.save(new Hospital("City Hospital", 5, 28.6200, 77.2100));
            repository.save(new Hospital("Apollo Hospital", 10, 28.6400, 77.2300));
            repository.save(new Hospital("AIIMS", 2, 28.5672, 77.2100));
            System.out.println("✅ Seeded 3 Hospitals with GPS coordinates and bed counts.");
        }
    }
}