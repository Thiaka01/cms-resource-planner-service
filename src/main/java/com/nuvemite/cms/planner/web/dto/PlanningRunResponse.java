package com.nuvemite.cms.planner.web.dto;

import com.nuvemite.cms.planner.planning.PlanningSuggestionPayload;
import java.util.List;
import java.util.UUID;

public record PlanningRunResponse(
        UUID runId, int suggestedStopCount, List<PlanningSuggestionPayload.DayPlan> days) {}
