package com.nuvemite.cms.planner.planning;

import com.nuvemite.cms.planner.config.PlannerProperties;
import com.nuvemite.cms.planner.domain.Driver;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.Inspector;
import com.nuvemite.cms.planner.domain.LicenseTypePlanningConfig;
import com.nuvemite.cms.planner.domain.PremiseLocationCache;
import com.nuvemite.cms.planner.domain.Vehicle;
import com.nuvemite.cms.planner.geo.GeoDistance;
import com.nuvemite.cms.planner.geo.InspectionNeighborService;
import com.nuvemite.cms.planner.repository.DriverRepository;
import com.nuvemite.cms.planner.repository.LicenseTypePlanningConfigRepository;
import com.nuvemite.cms.planner.repository.PremiseLocationCacheRepository;
import com.nuvemite.cms.planner.repository.VehicleRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PlanningEngine {

    private final PremiseLocationCacheRepository premiseLocationCacheRepository;
    private final LicenseTypePlanningConfigRepository configRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final InspectionNeighborService neighborService;
    private final PlannerProperties properties;

    public PlanningEngine(
            PremiseLocationCacheRepository premiseLocationCacheRepository,
            LicenseTypePlanningConfigRepository configRepository,
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository,
            InspectionNeighborService neighborService,
            PlannerProperties properties) {
        this.premiseLocationCacheRepository = premiseLocationCacheRepository;
        this.configRepository = configRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.neighborService = neighborService;
        this.properties = properties;
    }

    public PlanningSuggestionPayload buildSuggestions(
            List<InspectionRequest> requests, List<Inspector> inspectors) {
        if (requests.isEmpty()) {
            return new PlanningSuggestionPayload(List.of());
        }

        Map<String, Integer> durationByLicenseType = loadDurationConfig();
        Map<UUID, PremiseLocationCache> premiseLocations = loadPremiseLocations(requests, inspectors);

        Map<LocalDate, List<InspectionRequest>> byDay =
                requests.stream().collect(Collectors.groupingBy(InspectionRequest::getConfirmedDate));

        List<PlanningSuggestionPayload.DayPlan> dayPlans = new ArrayList<>();
        for (var entry : byDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .toList()) {
            dayPlans.add(planDay(entry.getKey(), entry.getValue(), inspectors, durationByLicenseType, premiseLocations));
        }
        return new PlanningSuggestionPayload(dayPlans);
    }

    private PlanningSuggestionPayload.DayPlan planDay(
            LocalDate visitDate,
            List<InspectionRequest> dayRequests,
            List<Inspector> inspectors,
            Map<String, Integer> durationByLicenseType,
            Map<UUID, PremiseLocationCache> premiseLocations) {

        int dayBudgetSeconds = properties.workingDayMinutes() * 60;
        List<InspectorRoute> routes = new ArrayList<>();
        Set<UUID> assigned = new HashSet<>();
        Set<UUID> usedDrivers = new HashSet<>();
        Set<UUID> usedVehicles = new HashSet<>();

        List<InspectionRequest> sorted = dayRequests.stream()
                .sorted(Comparator.comparing(InspectionRequest::getId))
                .toList();

        for (InspectionRequest request : sorted) {
            if (assigned.contains(request.getId())) {
                continue;
            }
            int inspectionMinutes = durationByLicenseType.getOrDefault(request.getLicenseType(), 120);

            Optional<InspectorRoute> bestExisting = findBestRouteToJoin(
                    routes, request, inspectionMinutes, dayBudgetSeconds, premiseLocations);
            if (bestExisting.isPresent()) {
                InspectorRoute route = bestExisting.get();
                appendStop(route, request, inspectionMinutes, premiseLocations);
                assigned.add(request.getId());
                continue;
            }

            Inspector inspector = pickInspectorForNewRoute(request, inspectors, routes, premiseLocations);
            if (inspector == null) {
                continue;
            }
            int travelFromHome = travelFromHomeSeconds(inspector, request, premiseLocations);
            if (travelFromHome + inspectionMinutes * 60L > dayBudgetSeconds) {
                continue;
            }
            InspectorRoute route = new InspectorRoute(inspector, visitDate);
            route.usedTravelSeconds = travelFromHome;
            route.usedInspectionMinutes = inspectionMinutes;
            route.stops.add(new StopEntry(request, inspectionMinutes, travelFromHome));
            assigned.add(request.getId());
            routes.add(route);
        }

        List<PlanningSuggestionPayload.VisitPlan> visits = new ArrayList<>();
        for (InspectorRoute route : routes) {
            UUID premiseId = route.inspector.getHomePremiseId();
            UUID driverId = pickDriver(premiseId, usedDrivers);
            UUID vehicleId = pickVehicle(premiseId, usedVehicles);
            if (driverId != null) {
                usedDrivers.add(driverId);
            }
            if (vehicleId != null) {
                usedVehicles.add(vehicleId);
            }
            List<PlanningSuggestionPayload.StopPlan> stopPlans = new ArrayList<>();
            int order = 1;
            for (StopEntry stop : route.stops) {
                stopPlans.add(new PlanningSuggestionPayload.StopPlan(
                        stop.request().getId(),
                        stop.request().getPremiseId(),
                        order++,
                        stop.inspectionMinutes(),
                        stop.travelFromPreviousSeconds()));
            }
            visits.add(new PlanningSuggestionPayload.VisitPlan(
                    route.inspector.getId(),
                    route.inspector.getFullName(),
                    premiseId,
                    driverId,
                    vehicleId,
                    stopPlans,
                    route.usedInspectionMinutes,
                    route.usedTravelSeconds));
        }
        return new PlanningSuggestionPayload.DayPlan(visitDate, visits);
    }

    private Optional<InspectorRoute> findBestRouteToJoin(
            List<InspectorRoute> routes,
            InspectionRequest request,
            int inspectionMinutes,
            int dayBudgetSeconds,
            Map<UUID, PremiseLocationCache> premiseLocations) {
        InspectorRoute best = null;
        int bestTravel = Integer.MAX_VALUE;
        for (InspectorRoute route : routes) {
            if (route.stops.isEmpty()) {
                continue;
            }
            boolean isNeighbor = route.stops.stream()
                    .anyMatch(s -> isNeighborPair(s.request().getId(), request.getId()));
            if (!isNeighbor) {
                continue;
            }
            UUID lastPremiseId =
                    route.stops.get(route.stops.size() - 1).request().getPremiseId();
            int travel = travelBetweenPremises(lastPremiseId, request.getPremiseId(), premiseLocations);
            int newTotal = route.usedTravelSeconds + travel + route.usedInspectionMinutes * 60 + inspectionMinutes * 60;
            if (newTotal <= dayBudgetSeconds && travel < bestTravel) {
                bestTravel = travel;
                best = route;
            }
        }
        return Optional.ofNullable(best);
    }

    private boolean isNeighborPair(UUID fromRequestId, UUID toRequestId) {
        Integer seconds = neighborService.travelSecondsBetween(fromRequestId, toRequestId);
        if (seconds != null) {
            return true;
        }
        return neighborService.travelSecondsBetween(toRequestId, fromRequestId) != null;
    }

    private void appendStop(
            InspectorRoute route,
            InspectionRequest request,
            int inspectionMinutes,
            Map<UUID, PremiseLocationCache> premiseLocations) {
        UUID lastPremiseId = route.stops.get(route.stops.size() - 1).request().getPremiseId();
        int travel = travelBetweenPremises(lastPremiseId, request.getPremiseId(), premiseLocations);
        route.stops.add(new StopEntry(request, inspectionMinutes, travel));
        route.usedTravelSeconds += travel;
        route.usedInspectionMinutes += inspectionMinutes;
    }

    private Inspector pickInspectorForNewRoute(
            InspectionRequest request,
            List<Inspector> inspectors,
            List<InspectorRoute> existingRoutes,
            Map<UUID, PremiseLocationCache> premiseLocations) {
        Set<UUID> busy = existingRoutes.stream().map(r -> r.inspector.getId()).collect(Collectors.toSet());
        Inspector best = null;
        int bestTravel = Integer.MAX_VALUE;
        for (Inspector inspector : inspectors) {
            if (busy.contains(inspector.getId())) {
                continue;
            }
            int travel = travelFromHomeSeconds(inspector, request, premiseLocations);
            if (travel < bestTravel) {
                bestTravel = travel;
                best = inspector;
            }
        }
        return best;
    }

    private int travelFromHomeSeconds(
            Inspector inspector, InspectionRequest request, Map<UUID, PremiseLocationCache> locations) {
        PremiseLocationCache home = locations.get(inspector.getHomePremiseId());
        PremiseLocationCache target = locations.get(request.getPremiseId());
        if (home == null || target == null) {
            return 3600;
        }
        int meters = GeoDistance.haversineMeters(
                home.getLatitude(), home.getLongitude(), target.getLatitude(), target.getLongitude());
        return GeoDistance.estimateTravelSeconds(meters);
    }

    private int travelBetweenPremises(
            UUID fromPremiseId, UUID toPremiseId, Map<UUID, PremiseLocationCache> locations) {
        PremiseLocationCache from = locations.get(fromPremiseId);
        PremiseLocationCache to = locations.get(toPremiseId);
        if (from == null || to == null) {
            return 1800;
        }
        int meters = GeoDistance.haversineMeters(
                from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude());
        return GeoDistance.estimateTravelSeconds(meters);
    }

    private UUID pickDriver(UUID homePremiseId, Set<UUID> used) {
        return driverRepository.findByHomePremiseIdAndActiveTrue(homePremiseId).stream()
                .map(Driver::getId)
                .filter(id -> !used.contains(id))
                .findFirst()
                .orElse(null);
    }

    private UUID pickVehicle(UUID homePremiseId, Set<UUID> used) {
        return vehicleRepository.findByHomePremiseIdAndActiveTrue(homePremiseId).stream()
                .map(Vehicle::getId)
                .filter(id -> !used.contains(id))
                .findFirst()
                .orElse(null);
    }

    private Map<String, Integer> loadDurationConfig() {
        Map<String, Integer> map = new HashMap<>();
        for (LicenseTypePlanningConfig c : configRepository.findAll()) {
            map.put(c.getLicenseType(), c.getExpectedDurationMinutes());
        }
        return map;
    }

    private Map<UUID, PremiseLocationCache> loadPremiseLocations(
            List<InspectionRequest> requests, List<Inspector> inspectors) {
        Map<UUID, PremiseLocationCache> map = new HashMap<>();
        Set<UUID> premiseIds = new HashSet<>();
        requests.forEach(r -> premiseIds.add(r.getPremiseId()));
        inspectors.forEach(i -> premiseIds.add(i.getHomePremiseId()));
        for (UUID premiseId : premiseIds) {
            premiseLocationCacheRepository.findById(premiseId).ifPresent(loc -> map.put(premiseId, loc));
        }
        return map;
    }

    private static class InspectorRoute {
        final Inspector inspector;
        final LocalDate visitDate;
        final List<StopEntry> stops = new ArrayList<>();
        int usedInspectionMinutes;
        int usedTravelSeconds;

        InspectorRoute(Inspector inspector, LocalDate visitDate) {
            this.inspector = inspector;
            this.visitDate = visitDate;
        }
    }

    private record StopEntry(InspectionRequest request, int inspectionMinutes, int travelFromPreviousSeconds) {}
}
