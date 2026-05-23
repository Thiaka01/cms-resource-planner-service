package com.nuvemite.cms.planner.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateVehicleRequest(
        @NotNull UUID homePremiseId,
        @NotBlank String registrationNumber,
        String vehicleType,
        String capacityNotes) {}
