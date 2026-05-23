package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.Vehicle;
import com.nuvemite.cms.planner.exception.ResourceNotFoundException;
import com.nuvemite.cms.planner.repository.VehicleRepository;
import com.nuvemite.cms.planner.web.dto.CreateVehicleRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Vehicle> listByPremise(UUID homePremiseId) {
        return repository.findByHomePremiseIdAndActiveTrue(homePremiseId);
    }

    @Transactional
    public Vehicle create(CreateVehicleRequest request) {
        return repository.save(Vehicle.create(
                request.homePremiseId(),
                request.registrationNumber(),
                request.vehicleType(),
                request.capacityNotes()));
    }

    @Transactional
    public void deactivate(UUID id) {
        Vehicle vehicle = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        vehicle.deactivate();
        repository.save(vehicle);
    }
}
