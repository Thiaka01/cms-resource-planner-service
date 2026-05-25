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

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "license_type_planning_config")
public class LicenseTypePlanningConfig {

    @Id
    @Column(name = "license_type")
    private String licenseType;

    @Column(name = "expected_duration_minutes", nullable = false)
    private int expectedDurationMinutes;

    @Column(name = "max_per_inspector_per_day")
    private Integer maxPerInspectorPerDay;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


    public static LicenseTypePlanningConfig create(
            String licenseType, int expectedDurationMinutes, Integer maxPerInspectorPerDay, String updatedBy) {
        LicenseTypePlanningConfig c = new LicenseTypePlanningConfig();
        c.licenseType = licenseType;
        c.expectedDurationMinutes = expectedDurationMinutes;
        c.maxPerInspectorPerDay = maxPerInspectorPerDay;
        c.updatedBy = updatedBy;
        c.updatedAt = Instant.now();
        return c;
    }

    public void update(int expectedDurationMinutes, Integer maxPerInspectorPerDay, String updatedBy) {
        this.expectedDurationMinutes = expectedDurationMinutes;
        this.maxPerInspectorPerDay = maxPerInspectorPerDay;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }



}
