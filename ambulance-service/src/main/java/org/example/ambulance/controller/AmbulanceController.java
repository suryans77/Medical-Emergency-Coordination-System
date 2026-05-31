package org.example.ambulance.controller;

import org.example.ambulance.service.AmbulanceService;
import org.example.shared.dtos.Ambulance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ambulance")
public class AmbulanceController {

    private final AmbulanceService service;

    public AmbulanceController(AmbulanceService service) {
        this.service = service;
    }

    @GetMapping("/available")
    public ResponseEntity<Ambulance> getFirstAvailable() {
        return service.getAvailableAmbulance()
                .map(amb -> new Ambulance(
                        amb.getId(),
                        amb.getLatitude(),
                        amb.getLongitude(),
                        amb.getStatus()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
