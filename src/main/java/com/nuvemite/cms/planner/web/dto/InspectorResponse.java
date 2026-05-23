package com.nuvemite.cms.planner.web.dto;

import java.time.Instant;
import java.util.UUID;

public record InspectorResponse(
        UUID id,
        UUID userId,
        String employeeCode,
        String fullName,
        String email,
        String phone,
        UUID homePremiseId,
        boolean active,
        Instant createdAt) {}
