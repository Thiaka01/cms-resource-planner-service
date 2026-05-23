package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.PremiseLocationCache;
import com.nuvemite.cms.planner.messaging.events.PremiseCreatedEvent;
import com.nuvemite.cms.planner.repository.PremiseLocationCacheRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PremiseLocationService {

    private final PremiseLocationCacheRepository repository;

    public PremiseLocationService(PremiseLocationCacheRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void upsertFromEvent(PremiseCreatedEvent event) {
        if (event.location() == null
                || event.location().latitude() == null
                || event.location().longitude() == null) {
            return;
        }
        repository
                .findById(event.premiseId())
                .ifPresentOrElse(
                        c -> {
                            c.update(event.location().latitude(), event.location().longitude());
                            repository.save(c);
                        },
                        () -> repository.save(PremiseLocationCache.of(
                                event.premiseId(),
                                event.location().latitude(),
                                event.location().longitude())));
    }
}
