package com.nuvemite.cms.planner.service;

import com.nuvemite.cms.planner.domain.DateStatus;
import com.nuvemite.cms.planner.domain.InspectionAuthorRole;
import com.nuvemite.cms.planner.domain.InspectionDateMessage;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.InspectionType;
import com.nuvemite.cms.planner.domain.ResourceStatus;
import com.nuvemite.cms.planner.exception.ConflictException;
import com.nuvemite.cms.planner.exception.ResourceNotFoundException;
import com.nuvemite.cms.planner.messaging.EventTypes;
import com.nuvemite.cms.planner.messaging.events.InspectionDateConfirmedEvent;
import com.nuvemite.cms.planner.messaging.events.InspectionDateProposedEvent;
import com.nuvemite.cms.planner.messaging.events.LicenseAnnualInspectionDueEvent;
import com.nuvemite.cms.planner.messaging.events.LicenseApplicationSubmittedEvent;
import com.nuvemite.cms.planner.repository.InspectionDateMessageRepository;
import com.nuvemite.cms.planner.repository.InspectionRequestRepository;
import com.nuvemite.cms.planner.web.dto.AddInspectionMessageRequest;
import com.nuvemite.cms.planner.web.dto.ProposeInspectionDateRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InspectionRequestService {

    private final InspectionRequestRepository requestRepository;
    private final InspectionDateMessageRepository messageRepository;
    private final OutboxService outboxService;

    public InspectionRequestService(
            InspectionRequestRepository requestRepository,
            InspectionDateMessageRepository messageRepository,
            OutboxService outboxService) {
        this.requestRepository = requestRepository;
        this.messageRepository = messageRepository;
        this.outboxService = outboxService;
    }

    @Transactional(readOnly = true)
    public Page<InspectionRequest> list(
            UUID companyId,
            boolean companyVisibleOnly,
            InspectionType type,
            DateStatus dateStatus,
            ResourceStatus resourceStatus,
            Pageable pageable) {
        return requestRepository.search(companyId, companyVisibleOnly, type, dateStatus, resourceStatus, pageable);
    }

    @Transactional(readOnly = true)
    public InspectionRequest get(UUID id) {
        return find(id);
    }

    @Transactional(readOnly = true)
    public List<InspectionDateMessage> messages(UUID requestId) {
        return messageRepository.findByInspectionRequestIdOrderByCreatedAtAsc(requestId);
    }

    @Transactional
    public InspectionRequest handleApplicationSubmitted(LicenseApplicationSubmittedEvent event) {
        if (requestRepository.findByLicenseApplicationId(event.applicationId()).isPresent()) {
            return requestRepository.findByLicenseApplicationId(event.applicationId()).orElseThrow();
        }
        LocalDate[] preferred = event.preferredInspectionDates() != null
                ? event.preferredInspectionDates().toArray(LocalDate[]::new)
                : new LocalDate[0];
        InspectionRequest request = InspectionRequest.createApplication(
                event.applicationId(), event.companyId(), event.premiseId(), event.licenseType(), preferred);
        return requestRepository.save(request);
    }

    @Transactional
    public InspectionRequest handleAnnualDue(LicenseAnnualInspectionDueEvent event) {
        InspectionRequest request = InspectionRequest.createAnnual(
                event.licenseGrantId(), event.companyId(), event.premiseId(), event.licenseType());
        return requestRepository.save(request);
    }

    @Transactional
    public InspectionDateMessage addCompanyMessage(UUID requestId, String authorUserId, AddInspectionMessageRequest body) {
        InspectionRequest request = find(requestId);
        if (!request.isCompanyVisible()) {
            throw new ConflictException("Cannot message on this inspection");
        }
        LocalDate[] attached = body.preferredDates() != null
                ? body.preferredDates().toArray(LocalDate[]::new)
                : null;
        if (attached != null && attached.length > 0) {
            request.updateCompanyPreferredDates(attached);
        }
        request.addMessageActivity();
        requestRepository.save(request);
        return saveMessage(requestId, InspectionAuthorRole.COMPANY, authorUserId, body.message(), attached);
    }

    @Transactional
    public InspectionDateMessage addPlannerMessage(UUID requestId, String authorUserId, AddInspectionMessageRequest body) {
        InspectionRequest request = find(requestId);
        request.addMessageActivity();
        requestRepository.save(request);
        LocalDate[] attached = body.preferredDates() != null
                ? body.preferredDates().toArray(LocalDate[]::new)
                : null;
        return saveMessage(requestId, InspectionAuthorRole.PLANNER, authorUserId, body.message(), attached);
    }

    @Transactional
    public InspectionRequest proposeDate(UUID requestId, ProposeInspectionDateRequest body) {
        InspectionRequest request = find(requestId);
        if (request.getInspectionType() == InspectionType.SPECIAL) {
            throw new ConflictException("Use special date assignment for SPECIAL inspections");
        }
        request.proposeDate(body.proposedDate());
        requestRepository.save(request);

        UUID eventId = UUID.randomUUID();
        if (request.isCompanyVisible()) {
            outboxService.enqueue(
                    "inspection_request",
                    request.getId(),
                    EventTypes.INSPECTION_DATE_PROPOSED,
                    new InspectionDateProposedEvent(
                            eventId,
                            request.getId(),
                            request.getCompanyId(),
                            request.getPremiseId(),
                            body.proposedDate()));
        }
        return request;
    }

    @Transactional
    public InspectionRequest confirmDateByCompany(UUID requestId) {
        InspectionRequest request = find(requestId);
        if (!request.isCompanyVisible()) {
            throw new ConflictException("Company cannot confirm this inspection");
        }
        request.confirmDateByCompany();
        requestRepository.save(request);
        publishDateConfirmed(request);
        return request;
    }

    @Transactional
    public InspectionRequest cancel(UUID requestId) {
        InspectionRequest request = find(requestId);
        request.cancel();
        return requestRepository.save(request);
    }

    private void publishDateConfirmed(InspectionRequest request) {
        UUID eventId = UUID.randomUUID();
        outboxService.enqueue(
                "inspection_request",
                request.getId(),
                EventTypes.INSPECTION_DATE_CONFIRMED,
                new InspectionDateConfirmedEvent(
                        eventId,
                        request.getId(),
                        request.getLicenseApplicationId(),
                        request.getCompanyId(),
                        request.getPremiseId(),
                        request.getConfirmedDate()));
    }

    private InspectionDateMessage saveMessage(
            UUID requestId,
            InspectionAuthorRole role,
            String authorUserId,
            String body,
            LocalDate[] attached) {
        return messageRepository.save(
                InspectionDateMessage.create(requestId, role, authorUserId, body, attached));
    }

    private InspectionRequest find(UUID id) {
        return requestRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inspection request not found"));
    }
}
