package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.DateStatus;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.InspectionType;
import com.nuvemite.cms.planner.domain.ResourceStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InspectionRequestRepository extends JpaRepository<InspectionRequest, UUID> {

    Optional<InspectionRequest> findByLicenseApplicationId(UUID licenseApplicationId);

    @Query(
            """
            SELECT r FROM InspectionRequest r
            WHERE (:companyId IS NULL OR r.companyId = :companyId)
              AND (:companyVisibleOnly = false OR r.companyVisible = true)
              AND (:inspectionType IS NULL OR r.inspectionType = :inspectionType)
              AND (:dateStatus IS NULL OR r.dateStatus = :dateStatus)
              AND (:resourceStatus IS NULL OR r.resourceStatus = :resourceStatus)
            """)
    Page<InspectionRequest> search(
            @Param("companyId") UUID companyId,
            @Param("companyVisibleOnly") boolean companyVisibleOnly,
            @Param("inspectionType") InspectionType inspectionType,
            @Param("dateStatus") DateStatus dateStatus,
            @Param("resourceStatus") ResourceStatus resourceStatus,
            Pageable pageable);

    @Query(
            """
            SELECT r FROM InspectionRequest r
            WHERE r.resourceStatus = 'UNALLOCATED'
              AND r.dateStatus IN ('CONFIRMED_BY_COMPANY', 'CONFIRMED_BY_PLANNER')
            """)
    java.util.List<InspectionRequest> findReadyForPlanning();

    @Query(
            """
            SELECT r FROM InspectionRequest r
            WHERE r.resourceStatus = 'UNALLOCATED'
              AND r.dateStatus IN ('CONFIRMED_BY_COMPANY', 'CONFIRMED_BY_PLANNER')
              AND r.confirmedDate >= :from
              AND r.confirmedDate <= :to
            """)
    java.util.List<InspectionRequest> findReadyForPlanningInRange(
            @Param("from") java.time.LocalDate from, @Param("to") java.time.LocalDate to);
}
