package com.nuvemite.cms.planner.web.dto;

public record LicenseTypeConfigResponse(
        String licenseType, int expectedDurationMinutes, Integer maxPerInspectorPerDay) {}
