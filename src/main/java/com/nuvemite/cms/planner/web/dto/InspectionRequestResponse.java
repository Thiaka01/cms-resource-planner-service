package com.nuvemite.cms.planner.web.dto;

import com.nuvemite.cms.planner.domain.DateStatus;
import com.nuvemite.cms.planner.domain.InspectionType;
import com.nuvemite.cms.planner.domain.ResourceStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InspectionRequestResponse(
        UUID id,
        InspectionType inspectionType,
        DateStatus dateStatus,
        ResourceStatus resourceStatus,
        UUID licenseApplicationId,
        UUID licenseGrantId,
        UUID complaintId,
        UUID companyId,
        UUID premiseId,
        String licenseType,
        List<LocalDate> companyPreferredDates,
        LocalDate plannerProposedDate,
        LocalDate confirmedDate,
        LocalDate scheduledDate,
        boolean companyVisible,
        Instant createdAt,
        Instant updatedAt) {}
