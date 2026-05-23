package com.nuvemite.cms.planner.planning;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Serialized into planning_suggestion.suggestion_json. */
public record PlanningSuggestionPayload(List<DayPlan> days) {

    public record DayPlan(LocalDate visitDate, List<VisitPlan> visits) {}

    public record VisitPlan(
            UUID inspectorId,
            String inspectorName,
            UUID homePremiseId,
            UUID driverId,
            UUID vehicleId,
            List<StopPlan> stops,
            int totalInspectionMinutes,
            int totalTravelSeconds) {}

    public record StopPlan(
            UUID inspectionRequestId,
            UUID premiseId,
            int sequenceOrder,
            int inspectionDurationMinutes,
            int travelFromPreviousSeconds) {}
}
