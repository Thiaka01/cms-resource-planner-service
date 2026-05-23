package com.nuvemite.cms.planner.web.dto;

import com.nuvemite.cms.planner.domain.InspectionAuthorRole;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InspectionDateMessageResponse(
        UUID id,
        UUID inspectionRequestId,
        InspectionAuthorRole authorRole,
        String authorUserId,
        String body,
        List<LocalDate> attachedPreferredDates,
        Instant createdAt) {}
