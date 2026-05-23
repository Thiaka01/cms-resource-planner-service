package com.nuvemite.cms.planner.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateDriverRequest(
        @NotNull UUID homePremiseId,
        @NotBlank String fullName,
        String licenseNumber,
        String phone) {}
