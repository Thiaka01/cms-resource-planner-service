package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.Driver;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.InspectionType;
import com.nuvemite.cms.planner.domain.Inspector;
import com.nuvemite.cms.planner.domain.PlannedVisit;
import com.nuvemite.cms.planner.domain.PlannedVisitStatus;
import com.nuvemite.cms.planner.domain.TransportAssignment;
import com.nuvemite.cms.planner.domain.Vehicle;
import com.nuvemite.cms.planner.domain.VisitStop;
import com.nuvemite.cms.planner.exception.ConflictException;
import com.nuvemite.cms.planner.exception.ResourceNotFoundException;
import com.nuvemite.cms.planner.messaging.EventTypes;
import com.nuvemite.cms.planner.messaging.events.VisitScheduledEvent;
import com.nuvemite.cms.planner.planning.PlanningSuggestionPayload;
import com.nuvemite.cms.planner.repository.DriverRepository;
import com.nuvemite.cms.planner.repository.InspectionRequestRepository;
import com.nuvemite.cms.planner.repository.InspectorRepository;
import com.nuvemite.cms.planner.repository.PlannedVisitRepository;
import com.nuvemite.cms.planner.repository.TransportAssignmentRepository;
import com.nuvemite.cms.planner.repository.VehicleRepository;
import com.nuvemite.cms.planner.repository.VisitStopRepository;
import com.nuvemite.cms.planner.web.dto.CalendarDayResponse;
import com.nuvemite.cms.planner.web.dto.CalendarVisitResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlannedVisitService {

    private static final List<PlannedVisitStatus> CALENDAR_STATUSES =
            List.of(PlannedVisitStatus.SUGGESTED, PlannedVisitStatus.CONFIRMED, PlannedVisitStatus.IN_PROGRESS);

    private final PlannedVisitRepository plannedVisitRepository;
    private final VisitStopRepository visitStopRepository;
    private final TransportAssignmentRepository transportAssignmentRepository;
    private final InspectionRequestRepository inspectionRequestRepository;
    private final InspectorRepository inspectorRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final OutboxService outboxService;

    public PlannedVisitService(
            PlannedVisitRepository plannedVisitRepository,
            VisitStopRepository visitStopRepository,
            TransportAssignmentRepository transportAssignmentRepository,
            InspectionRequestRepository inspectionRequestRepository,
            InspectorRepository inspectorRepository,
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository,
            OutboxService outboxService) {
        this.plannedVisitRepository = plannedVisitRepository;
        this.visitStopRepository = visitStopRepository;
        this.transportAssignmentRepository = transportAssignmentRepository;
        this.inspectionRequestRepository = inspectionRequestRepository;
        this.inspectorRepository = inspectorRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public List<UUID> createFromSuggestion(PlanningSuggestionPayload payload) {
        List<UUID> visitIds = new ArrayList<>();
        for (PlanningSuggestionPayload.DayPlan day : payload.days()) {
            for (PlanningSuggestionPayload.VisitPlan visitPlan : day.visits()) {
                PlannedVisit visit = PlannedVisit.create(day.visitDate(), visitPlan.inspectorId());
                visit.markSuggested();
                plannedVisitRepository.save(visit);

                int order = 1;
                for (PlanningSuggestionPayload.StopPlan stop : visitPlan.stops()) {
                    visitStopRepository.save(VisitStop.create(visit.getId(), stop.inspectionRequestId(), order++));
                    InspectionRequest request = inspectionRequestRepository
                            .findById(stop.inspectionRequestId())
                            .orElseThrow(() -> new ResourceNotFoundException("Inspection request not found"));
                    request.markSuggested();
                    inspectionRequestRepository.save(request);
                }

                if (visitPlan.driverId() != null && visitPlan.vehicleId() != null) {
                    transportAssignmentRepository.save(
                            TransportAssignment.create(visit.getId(), visitPlan.driverId(), visitPlan.vehicleId()));
                }
                visitIds.add(visit.getId());
            }
        }
        return visitIds;
    }

    @Transactional
    public PlannedVisit confirmVisit(UUID visitId) {
        PlannedVisit visit = plannedVisitRepository
                .findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Planned visit not found"));
        if (visit.getStatus() == PlannedVisitStatus.CONFIRMED) {
            return visit;
        }
        if (visit.getStatus() != PlannedVisitStatus.SUGGESTED) {
            throw new ConflictException("Only suggested visits can be confirmed");
        }

        Inspector inspector = inspectorRepository
                .findById(visit.getInspectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Inspector not found"));

        List<VisitStop> stops = visitStopRepository.findByPlannedVisitIdOrderBySequenceOrderAsc(visitId);
        for (VisitStop stop : stops) {
            InspectionRequest request = inspectionRequestRepository
                    .findById(stop.getInspectionRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inspection request not found"));
            request.setScheduledDate(visit.getVisitDate());
            inspectionRequestRepository.save(request);

            if (request.getInspectionType() == InspectionType.APPLICATION
                    && request.getLicenseApplicationId() != null) {
                UUID eventId = UUID.randomUUID();
                outboxService.enqueue(
                        "planned_visit",
                        visit.getId(),
                        EventTypes.VISIT_SCHEDULED,
                        new VisitScheduledEvent(
                                eventId,
                                request.getId(),
                                request.getLicenseApplicationId(),
                                request.getPremiseId(),
                                inspector.getId(),
                                visit.getVisitDate(),
                                inspector.getFullName()));
            }
        }

        visit.confirm();
        return plannedVisitRepository.save(visit);
    }

    @Transactional(readOnly = true)
    public PlannedVisit findVisit(UUID visitId) {
        return plannedVisitRepository
                .findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Planned visit not found"));
    }

    @Transactional(readOnly = true)
    public List<CalendarDayResponse> calendar(LocalDate from, LocalDate to) {
        List<PlannedVisit> visits = plannedVisitRepository.findByDateRangeAndStatuses(from, to, CALENDAR_STATUSES);
        return visits.stream()
                .map(v -> {
                    Inspector inspector = inspectorRepository.findById(v.getInspectorId()).orElse(null);
                    List<VisitStop> stops = visitStopRepository.findByPlannedVisitIdOrderBySequenceOrderAsc(v.getId());
                    var transport = transportAssignmentRepository.findByPlannedVisitId(v.getId());
                    String driverName = transport.map(t -> driverRepository.findById(t.getDriverId()))
                            .flatMap(opt -> opt.map(Driver::getFullName))
                            .orElse(null);
                    String vehicleReg = transport.map(t -> vehicleRepository.findById(t.getVehicleId()))
                            .flatMap(opt -> opt.map(Vehicle::getRegistrationNumber))
                            .orElse(null);
                    return new CalendarVisitResponse(
                            v.getId(),
                            v.getVisitDate(),
                            v.getInspectorId(),
                            inspector != null ? inspector.getFullName() : null,
                            v.getStatus(),
                            stops.size(),
                            driverName,
                            vehicleReg);
                })
                .collect(java.util.stream.Collectors.groupingBy(CalendarVisitResponse::visitDate))
                .entrySet()
                .stream()
                .map(e -> new CalendarDayResponse(e.getKey(), e.getValue()))
                .sorted(java.util.Comparator.comparing(CalendarDayResponse::date))
                .toList();
    }
}
