package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.domain.PlannedVisit;
import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.service.PlannedVisitService;
import com.nuvemite.cms.planner.repository.InspectorRepository;
import com.nuvemite.cms.planner.repository.VisitStopRepository;
import com.nuvemite.cms.planner.web.dto.PlannedVisitResponse;
import com.nuvemite.cms.planner.web.dto.VisitStopResponse;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planner/planned-visits")
public class PlannedVisitController {

    private final PlannedVisitService plannedVisitService;
    private final PlannerAccessService access;
    private final VisitStopRepository visitStopRepository;
    private final InspectorRepository inspectorRepository;

    public PlannedVisitController(
            PlannedVisitService plannedVisitService,
            PlannerAccessService access,
            VisitStopRepository visitStopRepository,
            InspectorRepository inspectorRepository) {
        this.plannedVisitService = plannedVisitService;
        this.access = access;
        this.visitStopRepository = visitStopRepository;
        this.inspectorRepository = inspectorRepository;
    }

    @GetMapping("/{id}")
    public PlannedVisitResponse get(@PathVariable UUID id) {
        access.requireRegulator();
        PlannedVisit visit = plannedVisitService
                .findVisit(id);
        String inspectorName = inspectorRepository
                .findById(visit.getInspectorId())
                .map(i -> i.getFullName())
                .orElse(null);
        var stops = visitStopRepository.findByPlannedVisitIdOrderBySequenceOrderAsc(id).stream()
                .map(s -> new VisitStopResponse(s.getInspectionRequestId(), s.getSequenceOrder()))
                .toList();
        return new PlannedVisitResponse(
                visit.getId(), visit.getVisitDate(), visit.getInspectorId(), inspectorName, visit.getStatus(), stops);
    }

    @PostMapping("/{id}/confirm")
    public PlannedVisitResponse confirm(@PathVariable UUID id) {
        access.requireRegulator();
        plannedVisitService.confirmVisit(id);
        return get(id);
    }
}
