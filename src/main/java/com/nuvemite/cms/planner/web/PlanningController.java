package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.security.SecurityUtils;
import com.nuvemite.cms.planner.service.PlanningService;
import com.nuvemite.cms.planner.web.dto.ApplySuggestionResponse;
import com.nuvemite.cms.planner.web.dto.PlanningRunRequest;
import com.nuvemite.cms.planner.web.dto.PlanningRunResponse;
import com.nuvemite.cms.planner.web.dto.PlanningSuggestionResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planner/planning")
public class PlanningController {

    private final PlanningService planningService;
    private final PlannerAccessService access;

    public PlanningController(PlanningService planningService, PlannerAccessService access) {
        this.planningService = planningService;
        this.access = access;
    }

    @PostMapping("/runs")
    public PlanningRunResponse startRun(@Valid @RequestBody PlanningRunRequest request) {
        access.requireRegulator();
        return planningService.startRun(request, SecurityUtils.currentSubject());
    }

    @GetMapping("/suggestions/{runId}")
    public PlanningSuggestionResponse getSuggestion(@PathVariable UUID runId) {
        access.requireRegulator();
        return planningService.getSuggestion(runId);
    }

    @PostMapping("/suggestions/{runId}/apply")
    public ApplySuggestionResponse applySuggestion(@PathVariable UUID runId) {
        access.requireRegulator();
        return new ApplySuggestionResponse(runId, planningService.applySuggestion(runId));
    }
}
