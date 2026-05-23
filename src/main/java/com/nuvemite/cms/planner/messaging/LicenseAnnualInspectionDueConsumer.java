package com.nuvemite.cms.planner.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.planner.messaging.events.LicenseAnnualInspectionDueEvent;
import com.nuvemite.cms.planner.service.InboxService;
import com.nuvemite.cms.planner.service.InspectionRequestService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LicenseAnnualInspectionDueConsumer {

    private final ObjectMapper objectMapper;
    private final InboxService inboxService;
    private final InspectionRequestService inspectionRequestService;

    public LicenseAnnualInspectionDueConsumer(
            ObjectMapper objectMapper, InboxService inboxService, InspectionRequestService inspectionRequestService) {
        this.objectMapper = objectMapper;
        this.inboxService = inboxService;
        this.inspectionRequestService = inspectionRequestService;
    }

    @KafkaListener(topics = EventTypes.LICENSE_ANNUAL_INSPECTION_DUE, groupId = EventTypes.CONSUMER_GROUP)
    @Transactional
    public void onAnnualDue(ConsumerRecord<String, String> record) throws Exception {
        String eventId = header(record, "eventId");
        if (eventId != null && inboxService.isProcessed(eventId)) {
            return;
        }
        LicenseAnnualInspectionDueEvent event = objectMapper.readValue(record.value(), LicenseAnnualInspectionDueEvent.class);
        inspectionRequestService.handleAnnualDue(event);
        inboxService.markProcessed(eventId != null ? eventId : event.eventId().toString());
    }

    private static String header(ConsumerRecord<String, String> record, String name) {
        var h = record.headers().lastHeader(name);
        return h != null ? new String(h.value()) : null;
    }
}
