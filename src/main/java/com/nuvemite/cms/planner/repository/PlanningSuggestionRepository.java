package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.PlanningSuggestion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanningSuggestionRepository extends JpaRepository<PlanningSuggestion, UUID> {}
