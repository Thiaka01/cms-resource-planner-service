package com.nuvemite.cms.planner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "inbox_processed_event")
@IdClass(InboxProcessedEvent.InboxId.class)
public class InboxProcessedEvent {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Id
    @Column(name = "consumer_group")
    private String consumerGroup;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    protected InboxProcessedEvent() {}

    public static InboxProcessedEvent of(String eventId, String consumerGroup) {
        InboxProcessedEvent e = new InboxProcessedEvent();
        e.eventId = eventId;
        e.consumerGroup = consumerGroup;
        e.processedAt = Instant.now();
        return e;
    }

    public static class InboxId implements Serializable {
        private String eventId;
        private String consumerGroup;

        public InboxId() {}

        public InboxId(String eventId, String consumerGroup) {
            this.eventId = eventId;
            this.consumerGroup = consumerGroup;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof InboxId that)) return false;
            return Objects.equals(eventId, that.eventId) && Objects.equals(consumerGroup, that.consumerGroup);
        }

        @Override
        public int hashCode() {
            return Objects.hash(eventId, consumerGroup);
        }
    }
}
