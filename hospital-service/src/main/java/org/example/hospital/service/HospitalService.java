package org.example.hospital.service;

import org.example.hospital.entity.Hospital;
import org.example.hospital.repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class HospitalService {

    private final HospitalRepository repository;

    public HospitalService(HospitalRepository repository) {
        this.repository = repository;
    }

    public List<Hospital> getAvailableHospitals(int minBeds) {
        return repository.findByAvailableBedsGreaterThan(minBeds - 1); // e.g., > 0
    }

    @Transactional
    public void reserveBed(UUID hospitalId) {
        Hospital hospital = repository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found: " + hospitalId));

        if (hospital.getAvailableBeds() <= 0) {
            throw new IllegalStateException("No available beds at hospital: " + hospitalId);
        }

        hospital.setAvailableBeds(hospital.getAvailableBeds() - 1);
        repository.save(hospital);
    }
}