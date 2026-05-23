package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.service.DriverService;
import com.nuvemite.cms.planner.web.dto.CreateDriverRequest;
import com.nuvemite.cms.planner.web.dto.DriverResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planner/drivers")
public class DriverController {

    private final DriverService driverService;
    private final PlannerAccessService access;
    private final PlannerMapper mapper;

    public DriverController(DriverService driverService, PlannerAccessService access, PlannerMapper mapper) {
        this.driverService = driverService;
        this.access = access;
        this.mapper = mapper;
    }

    @GetMapping
    public List<DriverResponse> list(@RequestParam UUID homePremiseId) {
        access.requireRegulator();
        return driverService.listByPremise(homePremiseId).stream().map(mapper::toDriverResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse create(@Valid @RequestBody CreateDriverRequest request) {
        access.requireRegulator();
        return mapper.toDriverResponse(driverService.create(request));
    }

    @PostMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) {
        access.requireRegulator();
        driverService.deactivate(id);
    }
}
