package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.PlannedVisitStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlannedVisitRepository extends JpaRepository<com.nuvemite.cms.planner.domain.PlannedVisit, UUID> {

    @Query(
            """
            SELECT COUNT(pv) > 0 FROM PlannedVisit pv
            WHERE pv.inspectorId = :inspectorId
              AND pv.visitDate = :visitDate
              AND pv.status IN :blockingStatuses
            """)
    boolean existsBlockingVisitForInspector(
            @Param("inspectorId") UUID inspectorId,
            @Param("visitDate") LocalDate visitDate,
            @Param("blockingStatuses") List<PlannedVisitStatus> blockingStatuses);

    @Query(
            """
            SELECT pv FROM PlannedVisit pv
            WHERE pv.visitDate >= :from AND pv.visitDate <= :to
              AND pv.status IN :statuses
            ORDER BY pv.visitDate, pv.inspectorId
            """)
    List<com.nuvemite.cms.planner.domain.PlannedVisit> findByDateRangeAndStatuses(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("statuses") List<PlannedVisitStatus> statuses);
}
