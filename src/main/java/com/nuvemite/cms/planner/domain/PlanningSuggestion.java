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
@Table(name = "planning_suggestion")
public class PlanningSuggestion {

    @Id
    private UUID id;

    @Column(name = "run_date_from", nullable = false)
    private LocalDate runDateFrom;

    @Column(name = "run_date_to", nullable = false)
    private LocalDate runDateTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanningSuggestionStatus status = PlanningSuggestionStatus.PENDING;

    @Column(name = "suggestion_json", nullable = false, columnDefinition = "jsonb")
    private String suggestionJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private String createdBy;

    protected PlanningSuggestion() {}

    public static PlanningSuggestion create(
            LocalDate from, LocalDate to, String suggestionJson, String createdBy) {
        PlanningSuggestion s = new PlanningSuggestion();
        s.id = UUID.randomUUID();
        s.runDateFrom = from;
        s.runDateTo = to;
        s.suggestionJson = suggestionJson;
        s.status = PlanningSuggestionStatus.READY;
        s.createdBy = createdBy;
        s.createdAt = Instant.now();
        return s;
    }

    public void markApplied() {
        this.status = PlanningSuggestionStatus.APPLIED;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getRunDateFrom() {
        return runDateFrom;
    }

    public LocalDate getRunDateTo() {
        return runDateTo;
    }

    public PlanningSuggestionStatus getStatus() {
        return status;
    }

    public String getSuggestionJson() {
        return suggestionJson;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
