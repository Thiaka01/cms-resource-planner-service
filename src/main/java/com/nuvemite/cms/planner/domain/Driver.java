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
@Table(name = "driver")
public class Driver {

    @Id
    private UUID id;

    @Column(name = "home_premise_id", nullable = false)
    private UUID homePremiseId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "license_number")
    private String licenseNumber;

    private String phone;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    public static Driver create(UUID homePremiseId, String fullName, String licenseNumber, String phone) {
        Driver d = new Driver();
        d.id = UUID.randomUUID();
        d.homePremiseId = homePremiseId;
        d.fullName = fullName;
        d.licenseNumber = licenseNumber;
        d.phone = phone;
        d.createdAt = Instant.now();
        return d;
    }






    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }
}
