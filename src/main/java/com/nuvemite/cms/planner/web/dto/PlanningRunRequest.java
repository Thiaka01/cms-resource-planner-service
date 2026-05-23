package com.nuvemite.cms.planner.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PlanningRunRequest(@NotNull LocalDate from, @NotNull LocalDate to) {}
