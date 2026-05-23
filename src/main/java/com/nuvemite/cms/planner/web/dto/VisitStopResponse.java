package com.nuvemite.cms.planner.web.dto;

import java.util.UUID;

public record VisitStopResponse(UUID inspectionRequestId, int sequenceOrder) {}
