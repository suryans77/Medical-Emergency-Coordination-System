package org.example.case_.repository;

import org.example.case_.entity.CaseCorrelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaseCorrelationRepository extends JpaRepository<CaseCorrelation, UUID> {
}
