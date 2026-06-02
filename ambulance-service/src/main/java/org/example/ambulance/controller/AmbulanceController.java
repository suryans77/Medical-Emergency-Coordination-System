package org.example.ambulance.controller;

import org.example.ambulance.entity.Ambulance;
import org.example.ambulance.service.AmbulanceService;
import org.example.shared.enums.AmbulanceStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ambulances")
public class AmbulanceController {

    private final AmbulanceService service;

    public AmbulanceController(AmbulanceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Ambulance>> getAmbulances(@RequestParam(required = false) AmbulanceStatus status) {
        if (status != null) {
            return ResponseEntity.ok(service.getByStatus(status));
        }
        return ResponseEntity.ok(service.getByStatus(null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        AmbulanceStatus newStatus = AmbulanceStatus.valueOf(body.get("status").toUpperCase());
        service.updateStatus(id, newStatus);
        return ResponseEntity.ok().build();
    }
}