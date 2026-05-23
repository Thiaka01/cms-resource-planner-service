package com.nuvemite.cms.planner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "visit_stop")
public class VisitStop {

    @Id
    private UUID id;

    @Column(name = "planned_visit_id", nullable = false)
    private UUID plannedVisitId;

    @Column(name = "inspection_request_id", nullable = false)
    private UUID inspectionRequestId;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    protected VisitStop() {}

    public static VisitStop create(UUID plannedVisitId, UUID inspectionRequestId, int sequenceOrder) {
        VisitStop s = new VisitStop();
        s.id = UUID.randomUUID();
        s.plannedVisitId = plannedVisitId;
        s.inspectionRequestId = inspectionRequestId;
        s.sequenceOrder = sequenceOrder;
        return s;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPlannedVisitId() {
        return plannedVisitId;
    }

    public UUID getInspectionRequestId() {
        return inspectionRequestId;
    }

    public int getSequenceOrder() {
        return sequenceOrder;
    }
}
