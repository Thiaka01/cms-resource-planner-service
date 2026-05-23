package com.nuvemite.cms.planner.web.dto;

import java.time.LocalDate;

public record AvailabilityDayResponse(LocalDate date, boolean available) {}
