package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.VisitStop;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitStopRepository extends JpaRepository<VisitStop, UUID> {

    List<VisitStop> findByPlannedVisitIdOrderBySequenceOrderAsc(UUID plannedVisitId);
}
