package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.TransportAssignment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportAssignmentRepository extends JpaRepository<TransportAssignment, UUID> {

    Optional<TransportAssignment> findByPlannedVisitId(UUID plannedVisitId);
}
