package com.nuvemite.cms.planner.geo;

import com.nuvemite.cms.planner.config.PlannerProperties;
import com.nuvemite.cms.planner.domain.InspectionNeighborRank;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.PremiseLocationCache;
import com.nuvemite.cms.planner.repository.InspectionNeighborRankRepository;
import com.nuvemite.cms.planner.repository.PremiseLocationCacheRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InspectionNeighborService {

    private final PremiseLocationCacheRepository premiseLocationCacheRepository;
    private final InspectionNeighborRankRepository neighborRankRepository;
    private final GeoapifyClient geoapifyClient;
    private final PlannerProperties properties;

    public InspectionNeighborService(
            PremiseLocationCacheRepository premiseLocationCacheRepository,
            InspectionNeighborRankRepository neighborRankRepository,
            GeoapifyClient geoapifyClient,
            PlannerProperties properties) {
        this.premiseLocationCacheRepository = premiseLocationCacheRepository;
        this.neighborRankRepository = neighborRankRepository;
        this.geoapifyClient = geoapifyClient;
        this.properties = properties;
    }

    @Transactional
    public void refreshNeighborsForRequests(List<InspectionRequest> requests) {
        Map<UUID, PremiseLocationCache> locations = loadLocations(requests);
        int topN = properties.geoapify().neighborTopN();
        for (InspectionRequest source : requests) {
            PremiseLocationCache srcLoc = locations.get(source.getPremiseId());
            if (srcLoc == null) {
                continue;
            }
            List<Candidate> candidates = new ArrayList<>();
            for (InspectionRequest other : requests) {
                if (other.getId().equals(source.getId())) {
                    continue;
                }
                PremiseLocationCache otherLoc = locations.get(other.getPremiseId());
                if (otherLoc == null) {
                    continue;
                }
                int meters = GeoDistance.haversineMeters(
                        srcLoc.getLatitude(),
                        srcLoc.getLongitude(),
                        otherLoc.getLatitude(),
                        otherLoc.getLongitude());
                candidates.add(new Candidate(other.getId(), otherLoc, meters));
            }
            candidates.sort(Comparator.comparingInt(Candidate::meters));
            List<Candidate> top = candidates.stream().limit(topN).toList();
            if (top.isEmpty()) {
                continue;
            }

            neighborRankRepository.deleteByInspectionRequestId(source.getId());

            List<RouteMatrixRequest.LatLng> sources =
                    List.of(new RouteMatrixRequest.LatLng(srcLoc.getLatitude(), srcLoc.getLongitude()));
            List<RouteMatrixRequest.LatLng> targets =
                    top.stream().map(c -> new RouteMatrixRequest.LatLng(c.loc.getLatitude(), c.loc.getLongitude())).toList();

            RouteMatrixResult matrix = geoapifyClient.matrix(new RouteMatrixRequest(sources, targets));
            int rank = 1;
            for (int i = 0; i < top.size(); i++) {
                Candidate c = top.get(i);
                int seconds = matrix.durationSeconds(0, i);
                int meters = matrix.distanceMeters(0, i);
                neighborRankRepository.save(InspectionNeighborRank.of(
                        source.getId(), c.requestId(), rank++, seconds, meters));
            }
        }
    }

    @Transactional(readOnly = true)
    public Integer travelSecondsBetween(UUID fromRequestId, UUID toRequestId) {
        return neighborRankRepository
                .findByInspectionRequestIdOrderByRankAsc(fromRequestId).stream()
                .filter(n -> n.getNeighborRequestId().equals(toRequestId))
                .map(InspectionNeighborRank::getTravelDurationSeconds)
                .findFirst()
                .orElse(null);
    }

    private Map<UUID, PremiseLocationCache> loadLocations(List<InspectionRequest> requests) {
        Map<UUID, PremiseLocationCache> map = new HashMap<>();
        for (InspectionRequest r : requests) {
            premiseLocationCacheRepository.findById(r.getPremiseId()).ifPresent(loc -> map.put(r.getPremiseId(), loc));
        }
        return map;
    }

    private record Candidate(UUID requestId, PremiseLocationCache loc, int meters) {}
}
