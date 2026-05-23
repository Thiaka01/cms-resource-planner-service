package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.service.VehicleService;
import com.nuvemite.cms.planner.web.dto.CreateVehicleRequest;
import com.nuvemite.cms.planner.web.dto.VehicleResponse;
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
@RequestMapping("/api/v1/planner/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final PlannerAccessService access;
    private final PlannerMapper mapper;

    public VehicleController(VehicleService vehicleService, PlannerAccessService access, PlannerMapper mapper) {
        this.vehicleService = vehicleService;
        this.access = access;
        this.mapper = mapper;
    }

    @GetMapping
    public List<VehicleResponse> list(@RequestParam UUID homePremiseId) {
        access.requireRegulator();
        return vehicleService.listByPremise(homePremiseId).stream().map(mapper::toVehicleResponse).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse create(@Valid @RequestBody CreateVehicleRequest request) {
        access.requireRegulator();
        return mapper.toVehicleResponse(vehicleService.create(request));
    }

    @PostMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) {
        access.requireRegulator();
        vehicleService.deactivate(id);
    }
}
