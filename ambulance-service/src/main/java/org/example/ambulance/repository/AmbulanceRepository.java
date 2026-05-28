package org.example.ambulance.repository;

import org.example.ambulance.entity.Ambulance;
import org.example.shared.enums.AmbulanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, UUID> {

    // This tells Spring Data JPA to automatically generate a SQL query equivalent to:
    // SELECT * FROM ambulances WHERE status = 'AVAILABLE' LIMIT 1;
    Optional<Ambulance> findFirstByStatus(AmbulanceStatus status);
}