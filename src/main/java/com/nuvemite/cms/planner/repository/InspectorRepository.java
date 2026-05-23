package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.Inspector;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectorRepository extends JpaRepository<Inspector, UUID> {

    List<Inspector> findByActiveTrue();

    List<Inspector> findByHomePremiseIdAndActiveTrue(UUID homePremiseId);
}
