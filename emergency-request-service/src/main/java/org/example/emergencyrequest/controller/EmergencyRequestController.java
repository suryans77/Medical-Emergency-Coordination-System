package org.example.emergencyrequest.controller;

import org.example.emergencyrequest.service.EmergencyRequestService;
import org.example.shared.dtos.EmergencyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/emergency")
public class EmergencyRequestController {

    private final EmergencyRequestService service;

    public EmergencyRequestController(EmergencyRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> receiveEmergencyRequest(@RequestBody EmergencyRequest requestDTO) {
        UUID generatedId = service.processEmergency(requestDTO);

        return ResponseEntity.ok("Emergency Request successfully received and registered. System ID: " + generatedId);
    }
}
