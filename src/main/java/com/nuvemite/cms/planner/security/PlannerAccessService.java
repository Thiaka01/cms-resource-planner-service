package com.nuvemite.cms.planner.security;

import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.exception.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class PlannerAccessService {

    public void requireRegulator() {
        if (!SecurityUtils.currentUser().isRegulator()) {
            throw new AccessDeniedException("Regulator role required");
        }
    }

    public void requireReadInspection(InspectionRequest request) {
        CmsUserPrincipal user = SecurityUtils.currentUser();
        if (user.isRegulator()) {
            return;
        }
        if (!request.isCompanyVisible()) {
            throw new AccessDeniedException("Inspection not available");
        }
        if (!user.canAccessPremise(request.getCompanyId(), request.getPremiseId())) {
            throw new AccessDeniedException("No access to this inspection request");
        }
    }

    public void requireCompanyInspection(InspectionRequest request) {
        requireReadInspection(request);
        if (SecurityUtils.currentUser().isRegulator()) {
            throw new AccessDeniedException("Company users only");
        }
    }

    public void requirePlannerInspection(InspectionRequest request) {
        requireRegulator();
        if (!request.isCompanyVisible() && request.getInspectionType() != com.nuvemite.cms.planner.domain.InspectionType.SPECIAL) {
            throw new AccessDeniedException("Invalid inspection access");
        }
    }
}
