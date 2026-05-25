package com.nuvemite.cms.planner.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "transport_assignment")
public class TransportAssignment {

    @Id
    private UUID id;

    @Column(name = "planned_visit_id", nullable = false, unique = true)
    private UUID plannedVisitId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;


    public static TransportAssignment create(UUID plannedVisitId, UUID driverId, UUID vehicleId) {
        TransportAssignment t = new TransportAssignment();
        t.id = UUID.randomUUID();
        t.plannedVisitId = plannedVisitId;
        t.driverId = driverId;
        t.vehicleId = vehicleId;
        t.assignedAt = Instant.now();
        return t;
    }




}
