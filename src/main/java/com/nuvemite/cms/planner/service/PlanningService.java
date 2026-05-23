package com.nuvemite.cms.planner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.Inspector;
import com.nuvemite.cms.planner.domain.PlanningSuggestion;
import com.nuvemite.cms.planner.domain.PlanningSuggestionStatus;
import com.nuvemite.cms.planner.exception.ConflictException;
import com.nuvemite.cms.planner.exception.ResourceNotFoundException;
import com.nuvemite.cms.planner.geo.InspectionNeighborService;
import com.nuvemite.cms.planner.planning.PlanningEngine;
import com.nuvemite.cms.planner.planning.PlanningSuggestionPayload;
import com.nuvemite.cms.planner.repository.InspectionRequestRepository;
import com.nuvemite.cms.planner.repository.InspectorRepository;
import com.nuvemite.cms.planner.repository.PlanningSuggestionRepository;
import com.nuvemite.cms.planner.web.dto.PlanningRunRequest;
import com.nuvemite.cms.planner.web.dto.PlanningRunResponse;
import com.nuvemite.cms.planner.web.dto.PlanningSuggestionResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanningService {

    private final InspectionRequestRepository inspectionRequestRepository;
    private final InspectorRepository inspectorRepository;
    private final PlanningSuggestionRepository suggestionRepository;
    private final InspectionNeighborService neighborService;
    private final PlanningEngine planningEngine;
    private final PlannedVisitService plannedVisitService;
    private final ObjectMapper objectMapper;

    public PlanningService(
            InspectionRequestRepository inspectionRequestRepository,
            InspectorRepository inspectorRepository,
            PlanningSuggestionRepository suggestionRepository,
            InspectionNeighborService neighborService,
            PlanningEngine planningEngine,
            PlannedVisitService plannedVisitService,
            ObjectMapper objectMapper) {
        this.inspectionRequestRepository = inspectionRequestRepository;
        this.inspectorRepository = inspectorRepository;
        this.suggestionRepository = suggestionRepository;
        this.neighborService = neighborService;
        this.planningEngine = planningEngine;
        this.plannedVisitService = plannedVisitService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PlanningRunResponse startRun(PlanningRunRequest request, String actor) {
        List<InspectionRequest> eligible =
                inspectionRequestRepository.findReadyForPlanningInRange(request.from(), request.to());
        if (eligible.isEmpty()) {
            PlanningSuggestionPayload emptyPayload = new PlanningSuggestionPayload(List.of());
            PlanningSuggestion empty = saveSuggestion(request, emptyPayload, actor);
            return toRunResponse(empty, emptyPayload);
        }

        neighborService.refreshNeighborsForRequests(eligible);
        List<Inspector> inspectors = inspectorRepository.findByActiveTrue();
        PlanningSuggestionPayload payload = planningEngine.buildSuggestions(eligible, inspectors);
        PlanningSuggestion suggestion = saveSuggestion(request, payload, actor);
        return toRunResponse(suggestion, payload);
    }

    @Transactional(readOnly = true)
    public PlanningSuggestionResponse getSuggestion(UUID runId) {
        PlanningSuggestion suggestion = findSuggestion(runId);
        return toSuggestionResponse(suggestion);
    }

    @Transactional
    public List<UUID> applySuggestion(UUID runId) {
        PlanningSuggestion suggestion = findSuggestion(runId);
        if (suggestion.getStatus() == PlanningSuggestionStatus.APPLIED) {
            throw new ConflictException("Suggestion already applied");
        }
        PlanningSuggestionPayload payload = parsePayload(suggestion.getSuggestionJson());
        List<UUID> visitIds = plannedVisitService.createFromSuggestion(payload);
        suggestion.markApplied();
        suggestionRepository.save(suggestion);
        return visitIds;
    }

    private PlanningSuggestion saveSuggestion(
            PlanningRunRequest request, PlanningSuggestionPayload payload, String actor) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            return suggestionRepository.save(
                    PlanningSuggestion.create(request.from(), request.to(), json, actor));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize planning suggestion", e);
        }
    }

    private PlanningSuggestionPayload parsePayload(String json) {
        try {
            return objectMapper.readValue(json, PlanningSuggestionPayload.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse planning suggestion", e);
        }
    }

    private PlanningSuggestion findSuggestion(UUID id) {
        return suggestionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planning suggestion not found"));
    }

    private PlanningRunResponse toRunResponse(PlanningSuggestion suggestion, PlanningSuggestionPayload payload) {
        return new PlanningRunResponse(suggestion.getId(), countStops(payload), payload.days());
    }

    private PlanningSuggestionResponse toSuggestionResponse(PlanningSuggestion suggestion) {
        PlanningSuggestionPayload payload = parsePayload(suggestion.getSuggestionJson());
        return new PlanningSuggestionResponse(
                suggestion.getId(),
                suggestion.getRunDateFrom(),
                suggestion.getRunDateTo(),
                suggestion.getStatus(),
                payload.days(),
                suggestion.getCreatedAt());
    }

    private int countStops(PlanningSuggestionPayload payload) {
        return payload.days().stream()
                .mapToInt(d -> d.visits().stream().mapToInt(v -> v.stops().size()).sum())
                .sum();
    }
}
