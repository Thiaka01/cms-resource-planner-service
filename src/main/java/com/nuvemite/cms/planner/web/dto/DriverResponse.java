package com.nuvemite.cms.planner.web.dto;

import java.util.UUID;

public record DriverResponse(
        UUID id, UUID homePremiseId, String fullName, String licenseNumber, String phone, boolean active) {}
