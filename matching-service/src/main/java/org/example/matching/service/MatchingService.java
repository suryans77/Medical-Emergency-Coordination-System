package org.example.matching.service;

import org.example.matching.client.ResourceClient;
import org.example.matching.entity.DispatchSaga;
import org.example.shared.enums.SagaState;
import org.example.matching.producer.DispatchProducer;
import org.example.matching.repository.SagaRepository;
import org.example.shared.enums.AmbulanceStatus;
import org.example.shared.events.DispatchAssigned;
import org.example.shared.events.HospitalAssigned;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class MatchingService {

    private final ResourceClient resourceClient;
    private final DispatchProducer producer;
    private final SagaRepository sagaRepository;

    public MatchingService(ResourceClient resourceClient, DispatchProducer producer, SagaRepository sagaRepository) {
        this.resourceClient = resourceClient;
        this.producer = producer;
        this.sagaRepository = sagaRepository;
    }

    public void processEmergency(UUID emergencyId, double emergencyLat, double emergencyLon) {
        // Initialize the saga save-point in the database
        DispatchSaga saga = new DispatchSaga(emergencyId.toString());
        saga = sagaRepository.save(saga);

        try {
            System.out.println("Finding resources for Emergency: " + emergencyId);

            List<Map<String, Object>> ambulances = resourceClient.fetchAvailableAmbulances(AmbulanceStatus.AVAILABLE.name());
            List<Map<String, Object>> locations = resourceClient.fetchLocations();

            UUID bestAmbulanceId = findClosestAmbulance(emergencyLat, emergencyLon, ambulances, locations);
            if (bestAmbulanceId == null) {
                throw new RuntimeException("No ambulances available!");
            }

            resourceClient.reserveAmbulance(bestAmbulanceId, AmbulanceStatus.RESERVED.name());
            System.out.println("Reserved Ambulance: " + bestAmbulanceId);

            // Update saga state
            saga.setAmbulanceId(bestAmbulanceId.toString());
            saga.setState(SagaState.AMBULANCE_RESERVED);
            sagaRepository.save(saga);

            List<Map<String, Object>> hospitals = resourceClient.fetchHospitals(1);

            UUID bestHospitalId = findClosestHospital(emergencyLat, emergencyLon, hospitals);
            if (bestHospitalId == null) {
                throw new RuntimeException("No hospitals available!");
            }

            resourceClient.reserveHospitalBed(bestHospitalId);
            System.out.println("Reserved Bed at Hospital: " + bestHospitalId);

            // Update saga state
            saga.setHospitalId(bestHospitalId.toString());
            saga.setState(SagaState.HOSPITAL_RESERVED);
            sagaRepository.save(saga);

            HospitalAssigned hospitalEvent =
                    new HospitalAssigned(UUID.randomUUID(),
                            Instant.now(), emergencyId, bestHospitalId, findHospitalName(bestHospitalId, hospitals));
            producer.publishHospitalAssigned(hospitalEvent);

            DispatchAssigned dispatch =
                    new DispatchAssigned(UUID.randomUUID(),
                            Instant.now(), emergencyId, bestAmbulanceId, bestHospitalId);
            producer.publishDispatch(dispatch);

            // Finalize saga state
            saga.setState(SagaState.COMPLETED);
            sagaRepository.save(saga);

        } catch (Exception e) {
            System.err.println("Match failed for Emergency " + emergencyId + ": " + e.getMessage());

            // The Saga Rollback Logic
            if (saga.getState() == SagaState.AMBULANCE_RESERVED) {
                compensateAmbulance(saga);
            } else {
                saga.setState(SagaState.FAILED);
                sagaRepository.save(saga);
            }
        }
    }

    private void compensateAmbulance(DispatchSaga saga) {
        try {
            resourceClient.releaseAmbulance(UUID.fromString(saga.getAmbulanceId()));
            System.out.println("⏪ ROLLBACK: Ambulance " + saga.getAmbulanceId() + " safely released.");
            saga.setState(SagaState.COMPENSATED);
        } catch (Exception ex) {
            System.err.println("CRITICAL: Failed to release ambulance during saga compensation!");
            saga.setState(SagaState.FAILED);
        }
        sagaRepository.save(saga);
    }

    private UUID findClosestAmbulance(
            double emergencyLat,
            double emergencyLon,
            List<Map<String, Object>> ambulances,
            List<Map<String, Object>> locations
    ) {
        if (ambulances == null || locations == null) {
            return null;
        }

        UUID closestId = null;
        double minDistance = Double.MAX_VALUE;

        for (Map<String, Object> ambulance : ambulances) {
            UUID id = readUuid(ambulance, "ambulanceId", "id");
            if (id == null) {
                continue;
            }

            Map<String, Object> location = locations.stream()
                    .filter(item -> id.equals(readUuid(item, "ambulanceId", "id")))
                    .findFirst()
                    .orElse(null);

            if (location != null) {
                double distance = calculateDistance(
                        emergencyLat,
                        emergencyLon,
                        readDouble(location, "latitude"),
                        readDouble(location, "longitude")
                );
                if (distance < minDistance) {
                    minDistance = distance;
                    closestId = id;
                }
            }
        }

        return closestId;
    }

    private UUID findClosestHospital(double emergencyLat, double emergencyLon, List<Map<String, Object>> hospitals) {
        if (hospitals == null) {
            return null;
        }

        UUID closestId = null;
        double minDistance = Double.MAX_VALUE;

        for (Map<String, Object> hospital : hospitals) {
            UUID id = readUuid(hospital, "hospitalId", "id");
            if (id == null) {
                continue;
            }

            double distance = calculateDistance(
                    // Keeping your exact calculations intact
                    emergencyLat,
                    emergencyLon,
                    readDouble(hospital, "latitude"),
                    readDouble(hospital, "longitude")
            );
            if (distance < minDistance) {
                minDistance = distance;
                closestId = id;
            }
        }

        return closestId;
    }

    private String findHospitalName(UUID hospitalId, List<Map<String, Object>> hospitals) {
        if (hospitals == null) {
            return "Assigned Hospital";
        }

        return hospitals.stream()
                .filter(hospital -> hospitalId.equals(readUuid(hospital, "hospitalId", "id")))
                .findFirst()
                .map(hospital -> readString(hospital, "hospitalName", readString(hospital, "name", "Assigned Hospital")))
                .orElse("Assigned Hospital");
    }

    private UUID readUuid(Map<String, Object> payload, String primaryKey, String fallbackKey) {
        Object value = payload.get(primaryKey);
        if (value == null && fallbackKey != null) {
            value = payload.get(fallbackKey);
        }
        if (value == null) {
            return null;
        }
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(String.valueOf(value));
    }

    private double readDouble(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private String readString(Map<String, Object> payload, String key, String fallback) {
        Object value = payload.get(key);
        return value == null ? fallback : String.valueOf(value);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int earthRadiusKm = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadiusKm * c;
    }
}