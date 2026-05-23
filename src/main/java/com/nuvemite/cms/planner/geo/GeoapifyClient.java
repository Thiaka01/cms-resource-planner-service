package com.nuvemite.cms.planner.geo;

import com.nuvemite.cms.planner.config.PlannerProperties;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeoapifyClient {

    private static final Logger log = LoggerFactory.getLogger(GeoapifyClient.class);

    private final PlannerProperties properties;
    private final RestClient restClient;

    public GeoapifyClient(PlannerProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().baseUrl(properties.geoapify().baseUrl()).build();
    }

    public RouteMatrixResult matrix(RouteMatrixRequest request) {
        if (request.sources().isEmpty() || request.targets().isEmpty()) {
            return RouteMatrixResult.unavailable();
        }
        if (properties.geoapify().apiKey() == null || properties.geoapify().apiKey().isBlank()) {
            return fallbackMatrix(request);
        }
        try {
            var body = new GeoapifyMatrixRequest(
                    "drive",
                    request.sources().stream()
                            .map(s -> GeoapifyMatrixRequest.point(s.longitude(), s.latitude()))
                            .toList(),
                    request.targets().stream()
                            .map(t -> GeoapifyMatrixRequest.point(t.longitude(), t.latitude()))
                            .toList());

            GeoapifyMatrixResponse response = restClient
                    .post()
                    .uri(uri -> uri.path("/v1/routematrix").queryParam("apiKey", properties.geoapify().apiKey()).build())
                    .body(body)
                    .retrieve()
                    .body(GeoapifyMatrixResponse.class);

            if (response == null || response.sources_to_targets() == null) {
                return fallbackMatrix(request);
            }
            return parseMatrix(response, request.sources().size(), request.targets().size());
        } catch (Exception e) {
            log.warn("Geoapify matrix call failed, using haversine fallback: {}", e.getMessage());
            return fallbackMatrix(request);
        }
    }

    private RouteMatrixResult parseMatrix(GeoapifyMatrixResponse response, int sourceCount, int targetCount) {
        int[][] durations = new int[sourceCount][targetCount];
        int[][] distances = new int[sourceCount][targetCount];
        for (int s = 0; s < sourceCount; s++) {
            List<GeoapifyMatrixResponse.MatrixCell> row = response.sources_to_targets().get(s);
            for (int t = 0; t < targetCount; t++) {
                GeoapifyMatrixResponse.MatrixCell cell = row.get(t);
                durations[s][t] = cell.time() != null ? cell.time() : 0;
                distances[s][t] = cell.distance() != null ? cell.distance() : 0;
            }
        }
        return new RouteMatrixResult(true, durations, distances);
    }

    private RouteMatrixResult fallbackMatrix(RouteMatrixRequest request) {
        int sCount = request.sources().size();
        int tCount = request.targets().size();
        int[][] durations = new int[sCount][tCount];
        int[][] distances = new int[sCount][tCount];
        for (int s = 0; s < sCount; s++) {
            RouteMatrixRequest.LatLng src = request.sources().get(s);
            for (int t = 0; t < tCount; t++) {
                RouteMatrixRequest.LatLng tgt = request.targets().get(t);
                int meters = GeoDistance.haversineMeters(src.latitude(), src.longitude(), tgt.latitude(), tgt.longitude());
                distances[s][t] = meters;
                durations[s][t] = GeoDistance.estimateTravelSeconds(meters);
            }
        }
        return new RouteMatrixResult(false, durations, distances);
    }
}
