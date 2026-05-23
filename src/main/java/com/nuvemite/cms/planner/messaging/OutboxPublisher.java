package com.nuvemite.cms.planner.messaging;

import com.nuvemite.cms.planner.config.PlannerProperties;
import com.nuvemite.cms.planner.domain.OutboxEvent;
import com.nuvemite.cms.planner.repository.OutboxEventRepository;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PlannerProperties properties;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            PlannerProperties properties) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${cms.planner.outbox.poll-interval-ms:5000}")
    @Transactional
    public void publishPending() {
        List<OutboxEvent> events = outboxEventRepository.findUnpublished(properties.outbox().batchSize());
        for (OutboxEvent event : events) {
            try {
                ProducerRecord<String, String> record = new ProducerRecord<>(
                        event.getEventType(), event.getAggregateId().toString(), event.getPayload());
                record.headers()
                        .add(new RecordHeader("eventId", event.getId().toString().getBytes(StandardCharsets.UTF_8)));
                kafkaTemplate.send(record).get();
                event.markPublished();
                outboxEventRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
            }
        }
    }
}
