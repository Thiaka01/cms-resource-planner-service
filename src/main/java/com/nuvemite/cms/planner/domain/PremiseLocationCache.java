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
@Table(name = "premise_location_cache")
public class PremiseLocationCache {

    @Id
    @Column(name = "premise_id")
    private UUID premiseId;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    public static PremiseLocationCache of(UUID premiseId, double latitude, double longitude) {
        PremiseLocationCache c = new PremiseLocationCache();
        c.premiseId = premiseId;
        c.latitude = latitude;
        c.longitude = longitude;
        c.updatedAt = Instant.now();
        return c;
    }

    public void update(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.updatedAt = Instant.now();
    }



}
