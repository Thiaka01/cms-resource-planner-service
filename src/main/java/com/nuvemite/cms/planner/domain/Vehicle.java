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
@Table(name = "vehicle")
public class Vehicle {

    @Id
    private UUID id;

    @Column(name = "home_premise_id", nullable = false)
    private UUID homePremiseId;

    @Column(name = "registration_number", nullable = false, unique = true)
    private String registrationNumber;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "capacity_notes")
    private String capacityNotes;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    public static Vehicle create(
            UUID homePremiseId, String registrationNumber, String vehicleType, String capacityNotes) {
        Vehicle v = new Vehicle();
        v.id = UUID.randomUUID();
        v.homePremiseId = homePremiseId;
        v.registrationNumber = registrationNumber;
        v.vehicleType = vehicleType;
        v.capacityNotes = capacityNotes;
        v.createdAt = Instant.now();
        return v;
    }






    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }
}
