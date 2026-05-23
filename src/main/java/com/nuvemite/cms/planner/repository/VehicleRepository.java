package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.Vehicle;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    List<Vehicle> findByHomePremiseIdAndActiveTrue(UUID homePremiseId);
}
