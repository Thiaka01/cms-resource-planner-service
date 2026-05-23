package com.nuvemite.cms.planner.web.dto;

import java.util.List;
import java.util.UUID;

public record ApplySuggestionResponse(UUID runId, List<UUID> plannedVisitIds) {}
