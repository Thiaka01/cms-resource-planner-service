package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.InboxProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxProcessedEventRepository
        extends JpaRepository<InboxProcessedEvent, InboxProcessedEvent.InboxId> {}
