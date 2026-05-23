package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.PlannedVisitStatus;
import com.nuvemite.cms.planner.repository.InspectorRepository;
import com.nuvemite.cms.planner.repository.PlannedVisitRepository;
import com.nuvemite.cms.planner.web.dto.AvailabilityDayResponse;
import com.nuvemite.cms.planner.web.dto.ResourceType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvailabilityService {

    private static final List<PlannedVisitStatus> BLOCKING =
            List.of(PlannedVisitStatus.SUGGESTED, PlannedVisitStatus.CONFIRMED, PlannedVisitStatus.IN_PROGRESS);

    private final PlannedVisitRepository plannedVisitRepository;
    private final InspectorRepository inspectorRepository;

    public AvailabilityService(PlannedVisitRepository plannedVisitRepository, InspectorRepository inspectorRepository) {
        this.plannedVisitRepository = plannedVisitRepository;
        this.inspectorRepository = inspectorRepository;
    }

    @Transactional(readOnly = true)
    public List<AvailabilityDayResponse> inspectorAvailability(UUID inspectorId, LocalDate from, LocalDate to) {
        inspectorRepository.findById(inspectorId).orElseThrow();
        List<AvailabilityDayResponse> days = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            boolean booked = plannedVisitRepository.existsBlockingVisitForInspector(inspectorId, d, BLOCKING);
            days.add(new AvailabilityDayResponse(d, !booked));
        }
        return days;
    }

    @Transactional(readOnly = true)
    public List<AvailabilityDayResponse> availability(ResourceType type, UUID resourceId, LocalDate from, LocalDate to) {
        if (type == ResourceType.INSPECTOR) {
            return inspectorAvailability(resourceId, from, to);
        }
        throw new IllegalArgumentException("Availability for " + type + " uses same day-level booking via planned visits");
    }
}
