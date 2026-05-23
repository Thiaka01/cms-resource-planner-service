package com.nuvemite.cms.planner.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.messaging.events.ComplaintInspectionRequestedEvent;
import com.nuvemite.cms.planner.repository.InspectionRequestRepository;
import com.nuvemite.cms.planner.service.InboxService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ComplaintInspectionRequestedConsumer {

    private final ObjectMapper objectMapper;
    private final InboxService inboxService;
    private final InspectionRequestRepository requestRepository;

    public ComplaintInspectionRequestedConsumer(
            ObjectMapper objectMapper,
            InboxService inboxService,
            InspectionRequestRepository requestRepository) {
        this.objectMapper = objectMapper;
        this.inboxService = inboxService;
        this.requestRepository = requestRepository;
    }

    @KafkaListener(topics = EventTypes.COMPLAINT_INSPECTION_REQUESTED, groupId = EventTypes.CONSUMER_GROUP)
    @Transactional
    public void onComplaint(ConsumerRecord<String, String> record) throws Exception {
        String eventId = header(record, "eventId");
        if (eventId != null && inboxService.isProcessed(eventId)) {
            return;
        }
        ComplaintInspectionRequestedEvent event = objectMapper.readValue(record.value(), ComplaintInspectionRequestedEvent.class);
        InspectionRequest request = InspectionRequest.createSpecial(
                event.complaintId(),
                event.companyId(),
                event.premiseId(),
                event.licenseType(),
                event.inspectionDate());
        requestRepository.save(request);
        inboxService.markProcessed(eventId != null ? eventId : event.eventId().toString());
    }

    private static String header(ConsumerRecord<String, String> record, String name) {
        var h = record.headers().lastHeader(name);
        return h != null ? new String(h.value()) : null;
    }
}
