package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.Inspector;
import com.nuvemite.cms.planner.exception.ResourceNotFoundException;
import com.nuvemite.cms.planner.repository.InspectorRepository;
import com.nuvemite.cms.planner.web.dto.CreateInspectorRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InspectorService {

    private final InspectorRepository repository;

    public InspectorService(InspectorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Inspector> listActive() {
        return repository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Inspector get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inspector not found"));
    }

    @Transactional
    public Inspector create(CreateInspectorRequest request) {
        Inspector inspector = Inspector.create(
                request.userId(),
                request.employeeCode(),
                request.fullName(),
                request.email(),
                request.phone(),
                request.homePremiseId());
        return repository.save(inspector);
    }

    @Transactional
    public void deactivate(UUID id) {
        Inspector inspector = get(id);
        inspector.deactivate();
        repository.save(inspector);
    }
}
