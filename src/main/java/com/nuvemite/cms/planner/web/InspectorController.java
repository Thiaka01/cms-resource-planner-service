package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.service.InspectorService;
import com.nuvemite.cms.planner.web.dto.CreateInspectorRequest;
import com.nuvemite.cms.planner.web.dto.InspectorResponse;
import com.nuvemite.cms.planner.web.mapper.PlannerMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planner/inspectors")
public class InspectorController {

    private final InspectorService inspectorService;
    private final PlannerAccessService access;
    private final PlannerMapper mapper;

    public InspectorController(InspectorService inspectorService, PlannerAccessService access, PlannerMapper mapper) {
        this.inspectorService = inspectorService;
        this.access = access;
        this.mapper = mapper;
    }

    @GetMapping
    public List<InspectorResponse> list() {
        access.requireRegulator();
        return inspectorService.listActive().stream().map(mapper::toInspectorResponse).toList();
    }

    @GetMapping("/{id}")
    public InspectorResponse get(@PathVariable UUID id) {
        access.requireRegulator();
        return mapper.toInspectorResponse(inspectorService.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InspectorResponse create(@Valid @RequestBody CreateInspectorRequest request) {
        access.requireRegulator();
        return mapper.toInspectorResponse(inspectorService.create(request));
    }
}
