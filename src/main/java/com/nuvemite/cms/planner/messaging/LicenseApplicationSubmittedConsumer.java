package com.nuvemite.cms.planner.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.planner.messaging.events.LicenseApplicationSubmittedEvent;
import com.nuvemite.cms.planner.service.InboxService;
import com.nuvemite.cms.planner.service.InspectionRequestService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LicenseApplicationSubmittedConsumer {

    private final ObjectMapper objectMapper;
    private final InboxService inboxService;
    private final InspectionRequestService inspectionRequestService;

    public LicenseApplicationSubmittedConsumer(
            ObjectMapper objectMapper, InboxService inboxService, InspectionRequestService inspectionRequestService) {
        this.objectMapper = objectMapper;
        this.inboxService = inboxService;
        this.inspectionRequestService = inspectionRequestService;
    }

    @KafkaListener(topics = EventTypes.LICENSE_APPLICATION_SUBMITTED, groupId = EventTypes.CONSUMER_GROUP)
    @Transactional
    public void onSubmitted(ConsumerRecord<String, String> record) throws Exception {
        String eventId = header(record, "eventId");
        if (eventId != null && inboxService.isProcessed(eventId)) {
            return;
        }
        LicenseApplicationSubmittedEvent event = objectMapper.readValue(record.value(), LicenseApplicationSubmittedEvent.class);
        inspectionRequestService.handleApplicationSubmitted(event);
        inboxService.markProcessed(eventId != null ? eventId : event.eventId().toString());
    }

    private static String header(ConsumerRecord<String, String> record, String name) {
        var h = record.headers().lastHeader(name);
        return h != null ? new String(h.value()) : null;
    }
}
