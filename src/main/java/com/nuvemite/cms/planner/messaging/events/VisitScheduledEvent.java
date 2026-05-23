package com.nuvemite.cms.planner.messaging.events;

import java.time.LocalDate;
import java.util.UUID;

public record VisitScheduledEvent(
        UUID eventId,
        UUID inspectionRequestId,
        UUID applicationId,
        UUID premiseId,
        UUID inspectorId,
        LocalDate visitDate,
        String inspectorName) {}
