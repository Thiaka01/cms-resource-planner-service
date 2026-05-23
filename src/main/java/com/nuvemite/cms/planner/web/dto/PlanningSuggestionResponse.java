package com.nuvemite.cms.planner.web.dto;

import com.nuvemite.cms.planner.domain.PlanningSuggestionStatus;
import com.nuvemite.cms.planner.planning.PlanningSuggestionPayload;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PlanningSuggestionResponse(
        UUID runId,
        LocalDate from,
        LocalDate to,
        PlanningSuggestionStatus status,
        List<PlanningSuggestionPayload.DayPlan> days,
        Instant createdAt) {}
