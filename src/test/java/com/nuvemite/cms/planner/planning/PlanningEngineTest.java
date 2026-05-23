package com.nuvemite.cms.planner.planning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.nuvemite.cms.planner.config.PlannerProperties;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.Inspector;
import com.nuvemite.cms.planner.domain.PremiseLocationCache;
import com.nuvemite.cms.planner.geo.InspectionNeighborService;
import com.nuvemite.cms.planner.repository.DriverRepository;
import com.nuvemite.cms.planner.repository.LicenseTypePlanningConfigRepository;
import com.nuvemite.cms.planner.repository.PremiseLocationCacheRepository;
import com.nuvemite.cms.planner.repository.VehicleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlanningEngineTest {

    @Mock
    private PremiseLocationCacheRepository premiseLocationCacheRepository;

    @Mock
    private LicenseTypePlanningConfigRepository configRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private InspectionNeighborService neighborService;

    private PlanningEngine engine;

    @BeforeEach
    void setUp() {
        PlannerProperties props = new PlannerProperties(
                new PlannerProperties.Outbox(5000, 50), 480, new PlannerProperties.Geoapify("", "", 10, ""));
        engine = new PlanningEngine(
                premiseLocationCacheRepository,
                configRepository,
                driverRepository,
                vehicleRepository,
                neighborService,
                props);
        when(configRepository.findAll()).thenReturn(List.of());
        when(driverRepository.findByHomePremiseIdAndActiveTrue(any())).thenReturn(List.of());
        when(vehicleRepository.findByHomePremiseIdAndActiveTrue(any())).thenReturn(List.of());
    }

    @Test
    void clustersRequestsOnSameDay() {
        UUID premiseA = UUID.randomUUID();
        UUID premiseB = UUID.randomUUID();
        UUID homePremise = UUID.randomUUID();
        stubLocation(premiseA, -1.28, 36.82);
        stubLocation(premiseB, -1.29, 36.83);
        stubLocation(homePremise, -1.30, 36.80);

        LocalDate day = LocalDate.now().plusDays(14);
        InspectionRequest r1 = InspectionRequest.createApplication(
                UUID.randomUUID(), UUID.randomUUID(), premiseA, "Importer", new LocalDate[] {day});
        r1.proposeDate(day);
        r1.confirmDateByCompany();
        InspectionRequest r2 = InspectionRequest.createApplication(
                UUID.randomUUID(), UUID.randomUUID(), premiseB, "Importer", new LocalDate[] {day});
        r2.proposeDate(day);
        r2.confirmDateByCompany();

        Inspector inspector = Inspector.create(
                UUID.randomUUID(), "EMP-1", "Inspector One", null, null, homePremise);

        PlanningSuggestionPayload payload = engine.buildSuggestions(List.of(r1, r2), List.of(inspector));

        assertThat(payload.days()).hasSize(1);
        assertThat(payload.days().get(0).visits()).isNotEmpty();
        int totalStops = payload.days().get(0).visits().stream()
                .mapToInt(v -> v.stops().size())
                .sum();
        assertThat(totalStops).isGreaterThanOrEqualTo(1);
    }

    private void stubLocation(UUID premiseId, double lon, double lat) {
        when(premiseLocationCacheRepository.findById(premiseId))
                .thenReturn(Optional.of(PremiseLocationCache.of(premiseId, lat, lon)));
    }
}
