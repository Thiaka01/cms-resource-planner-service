package com.nuvemite.cms.planner.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateInspectorRequest(
        @NotNull UUID userId,
        @NotBlank String employeeCode,
        @NotBlank String fullName,
        String email,
        String phone,
        @NotNull UUID homePremiseId) {}
