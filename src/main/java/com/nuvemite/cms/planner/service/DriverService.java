package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.Driver;
import com.nuvemite.cms.planner.exception.ResourceNotFoundException;
import com.nuvemite.cms.planner.repository.DriverRepository;
import com.nuvemite.cms.planner.web.dto.CreateDriverRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverService {

    private final DriverRepository repository;

    public DriverService(DriverRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Driver> listByPremise(UUID homePremiseId) {
        return repository.findByHomePremiseIdAndActiveTrue(homePremiseId);
    }

    @Transactional
    public Driver create(CreateDriverRequest request) {
        return repository.save(Driver.create(
                request.homePremiseId(), request.fullName(), request.licenseNumber(), request.phone()));
    }

    @Transactional
    public void deactivate(UUID id) {
        Driver driver = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        driver.deactivate();
        repository.save(driver);
    }
}
