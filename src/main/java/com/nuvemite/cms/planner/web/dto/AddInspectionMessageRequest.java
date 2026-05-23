package com.nuvemite.cms.planner.web.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

public record AddInspectionMessageRequest(@NotBlank String message, List<LocalDate> preferredDates) {}
