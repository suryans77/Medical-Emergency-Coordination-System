package org.example.hospital.controller;

import org.example.hospital.entity.Hospital;
import org.example.hospital.service.HospitalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hospitals")
public class HospitalController {

    private final HospitalService service;

    public HospitalController(HospitalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Hospital>> getAvailableHospitals(
            @RequestParam(defaultValue = "1") int minBeds) {
        return ResponseEntity.ok(service.getAvailableHospitals(minBeds));
    }

    @PatchMapping("/{id}/reserve-bed")
    public ResponseEntity<String> reserveBed(@PathVariable UUID id) {
        try {
            service.reserveBed(id);
            return ResponseEntity.ok("Bed reserved successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}