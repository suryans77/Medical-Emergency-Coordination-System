package org.example.notification.service;

import org.example.shared.events.DispatchAssigned;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void notifyPatient(DispatchAssigned event) {
        System.out.println("Ambulance assigned successfully");
        System.out.println("Emergency ID: " + event.emergencyId());
        System.out.println("Ambulance ID: " + event.ambulanceId());
    }
}
