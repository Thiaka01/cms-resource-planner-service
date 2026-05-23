package com.nuvemite.cms.planner.messaging.events;

import java.util.UUID;

public record PremiseCreatedEvent(
        UUID eventId,
        UUID premiseId,
        UUID companyId,
        LocationPayload location) {

    public record LocationPayload(Double latitude, Double longitude) {}
}
