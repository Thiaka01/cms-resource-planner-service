package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.Driver;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, UUID> {

    List<Driver> findByHomePremiseIdAndActiveTrue(UUID homePremiseId);
}
