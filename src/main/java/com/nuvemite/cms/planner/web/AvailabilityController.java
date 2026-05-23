package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.service.AvailabilityService;
import com.nuvemite.cms.planner.web.dto.AvailabilityDayResponse;
import com.nuvemite.cms.planner.web.dto.ResourceType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planner/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final PlannerAccessService access;

    public AvailabilityController(AvailabilityService availabilityService, PlannerAccessService access) {
        this.availabilityService = availabilityService;
        this.access = access;
    }

    @GetMapping
    public List<AvailabilityDayResponse> availability(
            @RequestParam ResourceType resourceType,
            @RequestParam UUID resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        access.requireRegulator();
        return availabilityService.availability(resourceType, resourceId, from, to);
    }
}
