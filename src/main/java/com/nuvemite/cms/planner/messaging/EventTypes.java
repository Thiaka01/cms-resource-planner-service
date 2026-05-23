package com.nuvemite.cms.planner.messaging;

public final class EventTypes {

    public static final String CONSUMER_GROUP = "cms-resource-planner";

    public static final String LICENSE_APPLICATION_SUBMITTED = "cms.license.application.submitted.v1";
    public static final String LICENSE_ANNUAL_INSPECTION_DUE = "cms.license.annual-inspection.due.v1";
    public static final String COMPLAINT_INSPECTION_REQUESTED = "cms.complaint.inspection.requested.v1";
    public static final String PREMISE_CREATED = "cms.premise.created.v1";

    public static final String INSPECTION_DATE_PROPOSED = "cms.inspection.date.proposed.v1";
    public static final String INSPECTION_DATE_CONFIRMED = "cms.inspection.date.confirmed.v1";
    public static final String VISIT_SCHEDULED = "cms.visit.scheduled.v1";

    private EventTypes() {}
}
