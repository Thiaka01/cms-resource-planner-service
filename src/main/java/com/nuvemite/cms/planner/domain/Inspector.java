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
@Table(name = "inspector")
public class Inspector {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String email;
    private String phone;

    @Column(name = "home_premise_id", nullable = false)
    private UUID homePremiseId;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    public static Inspector create(
            UUID userId,
            String employeeCode,
            String fullName,
            String email,
            String phone,
            UUID homePremiseId) {
        Inspector i = new Inspector();
        i.id = UUID.randomUUID();
        i.userId = userId;
        i.employeeCode = employeeCode;
        i.fullName = fullName;
        i.email = email;
        i.phone = phone;
        i.homePremiseId = homePremiseId;
        i.createdAt = Instant.now();
        return i;
    }








    public boolean isActive() {
        return active;
    }


    public void deactivate() {
        this.active = false;
    }
}
