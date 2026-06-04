package org.example.matching.service;

import org.example.matching.producer.DispatchProducer;
import org.example.shared.enums.AmbulanceStatus;
import org.example.shared.events.DispatchAssigned;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MatchingService {

    private final RestTemplate restTemplate;
    private final DispatchProducer producer;

    @Value("${services.ambulance-url}")
    private String ambulanceUrl;

    @Value("${services.location-url}")
    private String locationUrl;

    @Value("${services.hospital-url}")
    private String hospitalUrl;

    public MatchingService(RestTemplate restTemplate, DispatchProducer producer) {
        this.restTemplate = restTemplate;
        this.producer = producer;
    }

    public void processEmergency(UUID emergencyId, double emergencyLat, double emergencyLon) {
        try {
            System.out.println("🔍 Finding resources for Emergency: " + emergencyId);

            // 1. Get Available Ambulances
            List<Map<String, Object>> ambulances = restTemplate.exchange(
                    ambulanceUrl + "/ambulances?status=" + AmbulanceStatus.AVAILABLE,
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();

            // 2. Get Live Locations from Redis
            List<Map<String, Object>> locations = restTemplate.exchange(
                    locationUrl + "/locations",
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();

            // 3. Find Closest Ambulance
            UUID bestAmbulanceId = findClosestAmbulance(emergencyLat, emergencyLon, ambulances, locations);
            if (bestAmbulanceId == null) throw new RuntimeException("No ambulances available!");

            // 4. Reserve the Ambulance
            restTemplate.patchForObject(ambulanceUrl + "/ambulances/" + bestAmbulanceId + "/status",
                    Map.of("status", "RESERVED"), Void.class);
            System.out.println("✅ Reserved Ambulance: " + bestAmbulanceId);

            // 5. Get Available Hospitals
            List<Map<String, Object>> hospitals = restTemplate.exchange(
                    hospitalUrl + "/hospitals?minBeds=1",
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();

            // 6. Find Closest Hospital
            UUID bestHospitalId = findClosestHospital(emergencyLat, emergencyLon, hospitals);
            if (bestHospitalId == null) throw new RuntimeException("No hospitals available!");

            // 7. Reserve the Hospital Bed
            restTemplate.patchForObject(hospitalUrl + "/hospitals/" + bestHospitalId + "/reserve-bed",
                    null, String.class);
            System.out.println("✅ Reserved Bed at Hospital: " + bestHospitalId);

            // 8. Publish the fully-loaded Phase 2 Event
            DispatchAssigned dispatch = new DispatchAssigned(emergencyId, bestAmbulanceId, bestHospitalId);
            producer.publishDispatch(dispatch);

        } catch (Exception e) {
            System.err.println("❌ Match failed for Emergency " + emergencyId + ": " + e.getMessage());
        }
    }

    // --- Geolocation Math Helpers ---

    private UUID findClosestAmbulance(double eLat, double eLon, List<Map<String, Object>> ambulances, List<Map<String, Object>> locations) {
        UUID closestId = null;
        double minDistance = Double.MAX_VALUE;

        for (Map<String, Object> amb : ambulances) {
            UUID id = UUID.fromString((String) amb.get("id"));

            // Find this ambulance's coordinates in the location list
            Map<String, Object> loc = locations.stream()
                    .filter(l -> UUID.fromString((String) l.get("ambulanceId")).equals(id))
                    .findFirst().orElse(null);

            if (loc != null) {
                double dist = calculateDistance(eLat, eLon, (Double) loc.get("latitude"), (Double) loc.get("longitude"));
                if (dist < minDistance) {
                    minDistance = dist;
                    closestId = id;
                }
            }
        }
        return closestId;
    }

    private UUID findClosestHospital(double eLat, double eLon, List<Map<String, Object>> hospitals) {
        UUID closestId = null;
        double minDistance = Double.MAX_VALUE;

        for (Map<String, Object> hosp : hospitals) {
            UUID id = UUID.fromString((String) hosp.get("id"));
            double dist = calculateDistance(eLat, eLon, (Double) hosp.get("latitude"), (Double) hosp.get("longitude"));
            if (dist < minDistance) {
                minDistance = dist;
                closestId = id;
            }
        }
        return closestId;
    }

    // Haversine Formula for distance calculation
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}