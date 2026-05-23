package com.nuvemite.cms.planner.repository;

import com.nuvemite.cms.planner.domain.PremiseLocationCache;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiseLocationCacheRepository extends JpaRepository<PremiseLocationCache, UUID> {}
