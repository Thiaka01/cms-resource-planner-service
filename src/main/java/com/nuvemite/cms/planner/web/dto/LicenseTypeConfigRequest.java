package com.nuvemite.cms.planner.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LicenseTypeConfigRequest(
        @NotNull @Min(1) Integer expectedDurationMinutes, Integer maxPerInspectorPerDay) {}
