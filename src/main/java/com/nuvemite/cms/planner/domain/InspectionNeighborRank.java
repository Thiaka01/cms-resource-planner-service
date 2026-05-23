package com.nuvemite.cms.planner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "inspection_neighbor_rank")
@IdClass(InspectionNeighborRank.NeighborId.class)
public class InspectionNeighborRank {

    @Id
    @Column(name = "inspection_request_id")
    private UUID inspectionRequestId;

    @Id
    @Column(name = "neighbor_request_id")
    private UUID neighborRequestId;

    @Column(nullable = false)
    private int rank;

    @Column(name = "travel_duration_seconds")
    private Integer travelDurationSeconds;

    @Column(name = "travel_distance_meters")
    private Integer travelDistanceMeters;

    @Column(name = "computed_at", nullable = false)
    private Instant computedAt;

    protected InspectionNeighborRank() {}

    public static InspectionNeighborRank of(
            UUID inspectionRequestId,
            UUID neighborRequestId,
            int rank,
            Integer travelDurationSeconds,
            Integer travelDistanceMeters) {
        InspectionNeighborRank n = new InspectionNeighborRank();
        n.inspectionRequestId = inspectionRequestId;
        n.neighborRequestId = neighborRequestId;
        n.rank = rank;
        n.travelDurationSeconds = travelDurationSeconds;
        n.travelDistanceMeters = travelDistanceMeters;
        n.computedAt = Instant.now();
        return n;
    }

    public UUID getInspectionRequestId() {
        return inspectionRequestId;
    }

    public UUID getNeighborRequestId() {
        return neighborRequestId;
    }

    public int getRank() {
        return rank;
    }

    public Integer getTravelDurationSeconds() {
        return travelDurationSeconds;
    }

    public Integer getTravelDistanceMeters() {
        return travelDistanceMeters;
    }

    public static class NeighborId implements Serializable {
        private UUID inspectionRequestId;
        private UUID neighborRequestId;

        public NeighborId() {}

        public NeighborId(UUID inspectionRequestId, UUID neighborRequestId) {
            this.inspectionRequestId = inspectionRequestId;
            this.neighborRequestId = neighborRequestId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NeighborId that)) return false;
            return Objects.equals(inspectionRequestId, that.inspectionRequestId)
                    && Objects.equals(neighborRequestId, that.neighborRequestId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(inspectionRequestId, neighborRequestId);
        }
    }
}
