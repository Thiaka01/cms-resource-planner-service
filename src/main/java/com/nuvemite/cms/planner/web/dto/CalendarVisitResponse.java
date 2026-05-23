package com.nuvemite.cms.planner.web.dto;

import com.nuvemite.cms.planner.domain.PlannedVisitStatus;
import java.time.LocalDate;
import java.util.UUID;

public record CalendarVisitResponse(
        UUID plannedVisitId,
        LocalDate visitDate,
        UUID inspectorId,
        String inspectorName,
        PlannedVisitStatus status,
        int stopCount,
        String driverName,
        String vehicleRegistration) {}
