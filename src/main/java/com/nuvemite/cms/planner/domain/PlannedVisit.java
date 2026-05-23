package com.nuvemite.cms.planner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "planned_visit")
public class PlannedVisit {

    @Id
    private UUID id;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "inspector_id", nullable = false)
    private UUID inspectorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlannedVisitStatus status = PlannedVisitStatus.DRAFT;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PlannedVisit() {}

    public static PlannedVisit create(LocalDate visitDate, UUID inspectorId) {
        PlannedVisit v = new PlannedVisit();
        v.id = UUID.randomUUID();
        v.visitDate = visitDate;
        v.inspectorId = inspectorId;
        Instant now = Instant.now();
        v.createdAt = now;
        v.updatedAt = now;
        return v;
    }

    public void markSuggested() {
        this.status = PlannedVisitStatus.SUGGESTED;
        this.updatedAt = Instant.now();
    }

    public void confirm() {
        this.status = PlannedVisitStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public UUID getInspectorId() {
        return inspectorId;
    }

    public PlannedVisitStatus getStatus() {
        return status;
    }
}
