package com.nuvemite.cms.planner.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.planner.messaging.events.PremiseCreatedEvent;
import com.nuvemite.cms.planner.service.InboxService;
import com.nuvemite.cms.planner.service.PremiseLocationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PremiseCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final InboxService inboxService;
    private final PremiseLocationService premiseLocationService;

    public PremiseCreatedConsumer(
            ObjectMapper objectMapper, InboxService inboxService, PremiseLocationService premiseLocationService) {
        this.objectMapper = objectMapper;
        this.inboxService = inboxService;
        this.premiseLocationService = premiseLocationService;
    }

    @KafkaListener(topics = EventTypes.PREMISE_CREATED, groupId = EventTypes.CONSUMER_GROUP)
    @Transactional
    public void onPremiseCreated(ConsumerRecord<String, String> record) throws Exception {
        String eventId = header(record, "eventId");
        if (eventId != null && inboxService.isProcessed(eventId)) {
            return;
        }
        PremiseCreatedEvent event = objectMapper.readValue(record.value(), PremiseCreatedEvent.class);
        premiseLocationService.upsertFromEvent(event);
        inboxService.markProcessed(eventId != null ? eventId : event.eventId().toString());
    }

    private static String header(ConsumerRecord<String, String> record, String name) {
        var h = record.headers().lastHeader(name);
        return h != null ? new String(h.value()) : null;
    }
}
