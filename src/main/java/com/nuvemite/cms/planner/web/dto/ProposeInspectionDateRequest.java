package com.nuvemite.cms.planner.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProposeInspectionDateRequest(@NotNull LocalDate proposedDate) {}
