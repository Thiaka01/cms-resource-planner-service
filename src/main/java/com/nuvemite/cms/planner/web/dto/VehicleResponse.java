package com.nuvemite.cms.planner.web.dto;

import java.util.UUID;

public record VehicleResponse(
        UUID id,
        UUID homePremiseId,
        String registrationNumber,
        String vehicleType,
        String capacityNotes,
        boolean active) {}
