package com.nuvemite.cms.planner.web.mapper;

import com.nuvemite.cms.planner.domain.Driver;
import com.nuvemite.cms.planner.domain.InspectionDateMessage;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.Inspector;
import com.nuvemite.cms.planner.domain.LicenseTypePlanningConfig;
import com.nuvemite.cms.planner.domain.Vehicle;
import com.nuvemite.cms.planner.web.dto.InspectionDateMessageResponse;
import com.nuvemite.cms.planner.web.dto.InspectionRequestResponse;
import com.nuvemite.cms.planner.web.dto.InspectorResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PlannerMapper {

    public InspectorResponse toInspectorResponse(Inspector i) {
        return new InspectorResponse(
                i.getId(),
                i.getUserId(),
                i.getEmployeeCode(),
                i.getFullName(),
                i.getEmail(),
                i.getPhone(),
                i.getHomePremiseId(),
                i.isActive(),
                i.getCreatedAt());
    }

    public InspectionRequestResponse toInspectionResponse(InspectionRequest r) {
        return new InspectionRequestResponse(
                r.getId(),
                r.getInspectionType(),
                r.getDateStatus(),
                r.getResourceStatus(),
                r.getLicenseApplicationId(),
                r.getLicenseGrantId(),
                r.getComplaintId(),
                r.getCompanyId(),
                r.getPremiseId(),
                r.getLicenseType(),
                toList(r.getCompanyPreferredDates()),
                r.getPlannerProposedDate(),
                r.getConfirmedDate(),
                r.getScheduledDate(),
                r.isCompanyVisible(),
                r.getCreatedAt(),
                r.getUpdatedAt());
    }

    public InspectionDateMessageResponse toMessageResponse(InspectionDateMessage m) {
        return new InspectionDateMessageResponse(
                m.getId(),
                m.getInspectionRequestId(),
                m.getAuthorRole(),
                m.getAuthorUserId(),
                m.getBody(),
                toList(m.getAttachedPreferredDates()),
                m.getCreatedAt());
    }

    public com.nuvemite.cms.planner.web.dto.LicenseTypeConfigResponse toConfigResponse(LicenseTypePlanningConfig c) {
        return new com.nuvemite.cms.planner.web.dto.LicenseTypeConfigResponse(
                c.getLicenseType(), c.getExpectedDurationMinutes(), c.getMaxPerInspectorPerDay());
    }

    public com.nuvemite.cms.planner.web.dto.DriverResponse toDriverResponse(Driver d) {
        return new com.nuvemite.cms.planner.web.dto.DriverResponse(
                d.getId(), d.getHomePremiseId(), d.getFullName(), d.getLicenseNumber(), d.getPhone(), d.isActive());
    }

    public com.nuvemite.cms.planner.web.dto.VehicleResponse toVehicleResponse(Vehicle v) {
        return new com.nuvemite.cms.planner.web.dto.VehicleResponse(
                v.getId(),
                v.getHomePremiseId(),
                v.getRegistrationNumber(),
                v.getVehicleType(),
                v.getCapacityNotes(),
                v.isActive());
    }

    private static List<LocalDate> toList(LocalDate[] dates) {
        if (dates == null) {
            return List.of();
        }
        return Arrays.asList(dates);
    }
}
