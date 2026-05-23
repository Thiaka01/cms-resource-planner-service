package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.InspectionNeighborRank;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InspectionNeighborRankRepository
        extends JpaRepository<InspectionNeighborRank, InspectionNeighborRank.NeighborId> {

    List<InspectionNeighborRank> findByInspectionRequestIdOrderByRankAsc(UUID inspectionRequestId);

    @Modifying
    @Query("DELETE FROM InspectionNeighborRank n WHERE n.inspectionRequestId = :requestId")
    void deleteByInspectionRequestId(@Param("requestId") UUID requestId);
}
