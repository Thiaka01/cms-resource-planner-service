package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.InspectionDateMessage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionDateMessageRepository extends JpaRepository<InspectionDateMessage, UUID> {

    List<InspectionDateMessage> findByInspectionRequestIdOrderByCreatedAtAsc(UUID inspectionRequestId);
}
