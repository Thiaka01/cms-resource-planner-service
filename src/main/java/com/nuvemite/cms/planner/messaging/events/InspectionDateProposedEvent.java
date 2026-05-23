package com.nuvemite.cms.planner.messaging.events;

import java.time.LocalDate;
import java.util.UUID;

public record InspectionDateProposedEvent(
        UUID eventId,
        UUID inspectionRequestId,
        UUID companyId,
        UUID premiseId,
        LocalDate proposedDate) {}
