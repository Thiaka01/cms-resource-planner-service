package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.LicenseTypePlanningConfig;
import com.nuvemite.cms.planner.exception.ResourceNotFoundException;
import com.nuvemite.cms.planner.repository.LicenseTypePlanningConfigRepository;
import com.nuvemite.cms.planner.web.dto.LicenseTypeConfigRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LicenseTypeConfigService {

    private final LicenseTypePlanningConfigRepository repository;

    public LicenseTypeConfigService(LicenseTypePlanningConfigRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<LicenseTypePlanningConfig> list() {
        return repository.findAll();
    }

    @Transactional
    public LicenseTypePlanningConfig upsert(String licenseType, LicenseTypeConfigRequest request, String actor) {
        return repository
                .findById(licenseType)
                .map(existing -> {
                    existing.update(
                            request.expectedDurationMinutes(),
                            request.maxPerInspectorPerDay(),
                            actor);
                    return repository.save(existing);
                })
                .orElseGet(() -> repository.save(LicenseTypePlanningConfig.create(
                        licenseType,
                        request.expectedDurationMinutes(),
                        request.maxPerInspectorPerDay(),
                        actor)));
    }

    @Transactional(readOnly = true)
    public LicenseTypePlanningConfig get(String licenseType) {
        return repository
                .findById(licenseType)
                .orElseThrow(() -> new ResourceNotFoundException("Config not found for license type"));
    }
}
