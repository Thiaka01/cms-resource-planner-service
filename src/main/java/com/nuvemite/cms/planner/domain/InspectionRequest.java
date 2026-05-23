package com.nuvemite.cms.planner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "inspection_request")
public class InspectionRequest {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "inspection_type", nullable = false)
    private InspectionType inspectionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "date_status", nullable = false)
    private DateStatus dateStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_status", nullable = false)
    private ResourceStatus resourceStatus = ResourceStatus.UNALLOCATED;

    @Column(name = "license_application_id")
    private UUID licenseApplicationId;

    @Column(name = "license_grant_id")
    private UUID licenseGrantId;

    @Column(name = "complaint_id")
    private UUID complaintId;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "premise_id", nullable = false)
    private UUID premiseId;

    @Column(name = "license_type", nullable = false)
    private String licenseType;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "company_preferred_dates", columnDefinition = "date[]")
    private LocalDate[] companyPreferredDates;

    @Column(name = "planner_proposed_date")
    private LocalDate plannerProposedDate;

    @Column(name = "confirmed_date")
    private LocalDate confirmedDate;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "company_visible", nullable = false)
    private boolean companyVisible = true;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    protected InspectionRequest() {}

    public static InspectionRequest createApplication(
            UUID licenseApplicationId,
            UUID companyId,
            UUID premiseId,
            String licenseType,
            LocalDate[] preferredDates) {
        InspectionRequest r = new InspectionRequest();
        r.id = UUID.randomUUID();
        r.inspectionType = InspectionType.APPLICATION;
        r.dateStatus = DateStatus.PREFERRED_SUBMITTED;
        r.licenseApplicationId = licenseApplicationId;
        r.companyId = companyId;
        r.premiseId = premiseId;
        r.licenseType = licenseType;
        r.companyPreferredDates = preferredDates;
        r.companyVisible = true;
        Instant now = Instant.now();
        r.createdAt = now;
        r.updatedAt = now;
        return r;
    }

    public static InspectionRequest createAnnual(
            UUID licenseGrantId,
            UUID companyId,
            UUID premiseId,
            String licenseType) {
        InspectionRequest r = new InspectionRequest();
        r.id = UUID.randomUUID();
        r.inspectionType = InspectionType.ANNUAL;
        r.dateStatus = DateStatus.PREFERRED_SUBMITTED;
        r.licenseGrantId = licenseGrantId;
        r.companyId = companyId;
        r.premiseId = premiseId;
        r.licenseType = licenseType;
        r.companyVisible = true;
        Instant now = Instant.now();
        r.createdAt = now;
        r.updatedAt = now;
        return r;
    }

    public static InspectionRequest createSpecial(
            UUID complaintId, UUID companyId, UUID premiseId, String licenseType, LocalDate inspectionDate) {
        InspectionRequest r = new InspectionRequest();
        r.id = UUID.randomUUID();
        r.inspectionType = InspectionType.SPECIAL;
        r.dateStatus = DateStatus.CONFIRMED_BY_PLANNER;
        r.confirmedDate = inspectionDate;
        r.complaintId = complaintId;
        r.companyId = companyId;
        r.premiseId = premiseId;
        r.licenseType = licenseType;
        r.companyVisible = false;
        Instant now = Instant.now();
        r.createdAt = now;
        r.updatedAt = now;
        return r;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    public void addMessageActivity() {
        if (dateStatus == DateStatus.PREFERRED_SUBMITTED) {
            dateStatus = DateStatus.IN_DISCUSSION;
            touch();
        }
    }

    public void updateCompanyPreferredDates(LocalDate[] dates) {
        this.companyPreferredDates = dates;
        if (dateStatus == DateStatus.PROPOSED_BY_PLANNER) {
            dateStatus = DateStatus.IN_DISCUSSION;
        } else if (dateStatus == DateStatus.PREFERRED_SUBMITTED) {
            dateStatus = DateStatus.IN_DISCUSSION;
        }
        touch();
    }

    public void proposeDate(LocalDate date) {
        if (inspectionType == InspectionType.SPECIAL) {
            throw new IllegalStateException("Use internal special date assignment for SPECIAL inspections");
        }
        this.plannerProposedDate = date;
        this.dateStatus = DateStatus.PROPOSED_BY_PLANNER;
        touch();
    }

    public void confirmDateByCompany() {
        if (dateStatus != DateStatus.PROPOSED_BY_PLANNER) {
            throw new IllegalStateException("Company can only confirm when planner has proposed a date");
        }
        this.confirmedDate = plannerProposedDate;
        this.dateStatus = DateStatus.CONFIRMED_BY_COMPANY;
        touch();
    }

    public void confirmDateByPlanner(LocalDate date) {
        this.plannerProposedDate = date;
        this.confirmedDate = date;
        this.dateStatus = DateStatus.CONFIRMED_BY_PLANNER;
        touch();
    }

    public boolean isDateConfirmed() {
        return dateStatus == DateStatus.CONFIRMED_BY_COMPANY || dateStatus == DateStatus.CONFIRMED_BY_PLANNER;
    }

    public void cancel() {
        this.resourceStatus = ResourceStatus.CANCELLED;
        touch();
    }

    public UUID getId() {
        return id;
    }

    public InspectionType getInspectionType() {
        return inspectionType;
    }

    public DateStatus getDateStatus() {
        return dateStatus;
    }

    public ResourceStatus getResourceStatus() {
        return resourceStatus;
    }

    public UUID getLicenseApplicationId() {
        return licenseApplicationId;
    }

    public UUID getLicenseGrantId() {
        return licenseGrantId;
    }

    public UUID getComplaintId() {
        return complaintId;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getPremiseId() {
        return premiseId;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public LocalDate[] getCompanyPreferredDates() {
        return companyPreferredDates;
    }

    public LocalDate getPlannerProposedDate() {
        return plannerProposedDate;
    }

    public LocalDate getConfirmedDate() {
        return confirmedDate;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void markSuggested() {
        this.resourceStatus = ResourceStatus.SUGGESTED;
        touch();
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
        this.resourceStatus = ResourceStatus.SCHEDULED;
        touch();
    }

    public boolean isCompanyVisible() {
        return companyVisible;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
