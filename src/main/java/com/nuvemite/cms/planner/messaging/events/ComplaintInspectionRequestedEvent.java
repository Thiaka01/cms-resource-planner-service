package com.nuvemite.cms.planner.messaging.events;

import java.time.LocalDate;
import java.util.UUID;

public record ComplaintInspectionRequestedEvent(
        UUID eventId,
        UUID complaintId,
        UUID companyId,
        UUID premiseId,
        String licenseType,
        LocalDate inspectionDate) {}
