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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "inspection_date_message")
public class InspectionDateMessage {

    @Id
    private UUID id;

    @Column(name = "inspection_request_id", nullable = false)
    private UUID inspectionRequestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_role", nullable = false)
    private InspectionAuthorRole authorRole;

    @Column(name = "author_user_id", nullable = false)
    private String authorUserId;

    @Column(nullable = false)
    private String body;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "attached_preferred_dates", columnDefinition = "date[]")
    private LocalDate[] attachedPreferredDates;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected InspectionDateMessage() {}

    public static InspectionDateMessage create(
            UUID inspectionRequestId,
            InspectionAuthorRole authorRole,
            String authorUserId,
            String body,
            LocalDate[] attachedPreferredDates) {
        InspectionDateMessage m = new InspectionDateMessage();
        m.id = UUID.randomUUID();
        m.inspectionRequestId = inspectionRequestId;
        m.authorRole = authorRole;
        m.authorUserId = authorUserId;
        m.body = body;
        m.attachedPreferredDates = attachedPreferredDates;
        m.createdAt = Instant.now();
        return m;
    }

    public UUID getId() {
        return id;
    }

    public UUID getInspectionRequestId() {
        return inspectionRequestId;
    }

    public InspectionAuthorRole getAuthorRole() {
        return authorRole;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public String getBody() {
        return body;
    }

    public LocalDate[] getAttachedPreferredDates() {
        return attachedPreferredDates;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
