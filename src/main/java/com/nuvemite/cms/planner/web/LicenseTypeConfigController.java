package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.security.SecurityUtils;
import com.nuvemite.cms.planner.service.LicenseTypeConfigService;
import com.nuvemite.cms.planner.web.dto.LicenseTypeConfigRequest;
import com.nuvemite.cms.planner.web.dto.LicenseTypeConfigResponse;
import com.nuvemite.cms.planner.web.mapper.PlannerMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planner/license-type-config")
public class LicenseTypeConfigController {

    private final LicenseTypeConfigService configService;
    private final PlannerAccessService access;
    private final PlannerMapper mapper;

    public LicenseTypeConfigController(
            LicenseTypeConfigService configService, PlannerAccessService access, PlannerMapper mapper) {
        this.configService = configService;
        this.access = access;
        this.mapper = mapper;
    }

    @GetMapping
    public List<LicenseTypeConfigResponse> list() {
        access.requireRegulator();
        return configService.list().stream().map(mapper::toConfigResponse).toList();
    }

    @GetMapping("/{licenseType}")
    public LicenseTypeConfigResponse get(@PathVariable String licenseType) {
        access.requireRegulator();
        return mapper.toConfigResponse(configService.get(licenseType));
    }

    @PutMapping("/{licenseType}")
    public LicenseTypeConfigResponse upsert(
            @PathVariable String licenseType, @Valid @RequestBody LicenseTypeConfigRequest request) {
        access.requireRegulator();
        return mapper.toConfigResponse(
                configService.upsert(licenseType, request, SecurityUtils.currentSubject()));
    }
}
