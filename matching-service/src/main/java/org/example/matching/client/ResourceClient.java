package org.example.matching.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class ResourceClient {

    private final RestTemplate restTemplate;

    @Value("${services.ambulance-url}")
    private String ambulanceUrl;

    @Value("${services.location-url}")
    private String locationUrl;

    @Value("${services.hospital-url}")
    private String hospitalUrl;

    public ResourceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 🛡️ THE CUSTOM SHOCK ABSORBER
    // This takes ANY method and forces it to retry 3 times with exponential backoff
    private <T> T executeWithRetry(Supplier<T> networkCall, String operationName) {
        int maxAttempts = 3;
        long backoffMs = 1000; // Start with a 1-second delay

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return networkCall.get(); // Execute the network call

            } catch (RestClientException e) {
                if (attempt == maxAttempts) {
                    System.err.println("❌ [" + operationName + "] Failed completely after 3 attempts.");
                    throw e; // We are out of tries, let the Outbox fail and roll back
                }

                System.out.println("⚠️ [" + operationName + "] Network glitch. Retrying attempt " + (attempt + 1) + " in " + backoffMs + "ms...");

                try {
                    Thread.sleep(backoffMs); // Pause the thread
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry sleep interrupted", ie);
                }

                backoffMs *= 2; // Exponential backoff: 1000ms -> 2000ms -> 4000ms
            }
        }
        return null;
    }

    // --- YOUR NETWORK CALLS WRAPPED IN THE RETRY ENGINE ---

    public List<Map<String, Object>> fetchAvailableAmbulances(String status) {
        return executeWithRetry(() -> {
            System.out.println("🌐 Network Call: Fetching Ambulances...");
            return restTemplate.exchange(
                    ambulanceUrl + "/ambulances?status=" + status,
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
        }, "Fetch Ambulances");
    }

    public List<Map<String, Object>> fetchLocations() {
        return executeWithRetry(() -> {
            System.out.println("🌐 Network Call: Fetching Locations...");
            return restTemplate.exchange(
                    locationUrl + "/locations",
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
        }, "Fetch Locations");
    }

    public List<Map<String, Object>> fetchHospitals(int minBeds) {
        return executeWithRetry(() -> {
            System.out.println("🌐 Network Call: Fetching Hospitals...");
            return restTemplate.exchange(
                    hospitalUrl + "/hospitals?minBeds=" + minBeds,
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            ).getBody();
        }, "Fetch Hospitals");
    }

    // For Void methods, we just return a dummy value (true) to satisfy the Supplier
    public void reserveAmbulance(UUID ambulanceId, String status) {
        executeWithRetry(() -> {
            System.out.println("🌐 Network Call: Reserving Ambulance " + ambulanceId + "...");
            restTemplate.patchForObject(
                    ambulanceUrl + "/ambulances/" + ambulanceId + "/status",
                    Map.of("status", status),
                    Void.class
            );
            return true;
        }, "Reserve Ambulance");
    }

    public void reserveHospitalBed(UUID hospitalId) {
        executeWithRetry(() -> {
            System.out.println("🌐 Network Call: Reserving bed at Hospital " + hospitalId + "...");
            restTemplate.patchForObject(
                    hospitalUrl + "/hospitals/" + hospitalId + "/reserve-bed",
                    null,
                    String.class
            );
            return true;
        }, "Reserve Hospital");
    }

    public void releaseAmbulance(UUID ambulanceId) {
        executeWithRetry(() -> {
            System.out.println("⏪ ROLLBACK: Releasing Ambulance " + ambulanceId + " back to AVAILABLE...");
            restTemplate.patchForObject(
                    ambulanceUrl + "/ambulances/" + ambulanceId + "/status",
                    Map.of("status", "AVAILABLE"), // Put it back!
                    Void.class
            );
            return true;
        }, "Release Ambulance");
    }
}