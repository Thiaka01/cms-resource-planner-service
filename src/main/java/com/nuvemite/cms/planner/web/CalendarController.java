package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.service.PlannedVisitService;
import com.nuvemite.cms.planner.web.dto.CalendarDayResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planner/calendar")
public class CalendarController {

    private final PlannedVisitService plannedVisitService;
    private final PlannerAccessService access;

    public CalendarController(PlannedVisitService plannedVisitService, PlannerAccessService access) {
        this.plannedVisitService = plannedVisitService;
        this.access = access;
    }

    @GetMapping
    public List<CalendarDayResponse> calendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        access.requireRegulator();
        return plannedVisitService.calendar(from, to);
    }
}
