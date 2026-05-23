package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.InboxProcessedEvent;
import com.nuvemite.cms.planner.messaging.EventTypes;
import com.nuvemite.cms.planner.repository.InboxProcessedEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InboxService {

    private final InboxProcessedEventRepository repository;

    public InboxService(InboxProcessedEventRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public boolean isProcessed(String eventId) {
        return repository.existsById(new InboxProcessedEvent.InboxId(eventId, EventTypes.CONSUMER_GROUP));
    }

    @Transactional
    public void markProcessed(String eventId) {
        repository.save(InboxProcessedEvent.of(eventId, EventTypes.CONSUMER_GROUP));
    }
}
