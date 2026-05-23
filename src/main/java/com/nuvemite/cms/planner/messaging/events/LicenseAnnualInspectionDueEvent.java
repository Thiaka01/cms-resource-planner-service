package com.nuvemite.cms.planner.messaging.events;

import java.util.UUID;

public record LicenseAnnualInspectionDueEvent(
        UUID eventId,
        UUID licenseGrantId,
        UUID companyId,
        UUID premiseId,
        String licenseType) {}
