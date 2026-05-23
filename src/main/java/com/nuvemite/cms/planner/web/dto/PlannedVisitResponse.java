package com.nuvemite.cms.planner.web.dto;

import com.nuvemite.cms.planner.domain.PlannedVisitStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PlannedVisitResponse(
        UUID id,
        LocalDate visitDate,
        UUID inspectorId,
        String inspectorName,
        PlannedVisitStatus status,
        List<VisitStopResponse> stops) {}
