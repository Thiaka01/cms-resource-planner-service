package com.nuvemite.cms.planner.web.dto;

import java.time.LocalDate;
import java.util.List;

public record CalendarDayResponse(LocalDate date, List<CalendarVisitResponse> visits) {}
