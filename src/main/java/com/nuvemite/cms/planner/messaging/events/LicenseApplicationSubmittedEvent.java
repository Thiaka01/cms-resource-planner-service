package com.nuvemite.cms.planner.messaging.events;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record LicenseApplicationSubmittedEvent(
        UUID eventId,
        UUID applicationId,
        UUID companyId,
        UUID premiseId,
        String licenseType,
        List<LocalDate> preferredInspectionDates) {}
