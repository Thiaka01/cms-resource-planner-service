package com.nuvemite.cms.planner.web;

import com.nuvemite.cms.planner.domain.DateStatus;
import com.nuvemite.cms.planner.domain.InspectionRequest;
import com.nuvemite.cms.planner.domain.InspectionType;
import com.nuvemite.cms.planner.domain.ResourceStatus;
import com.nuvemite.cms.planner.security.PlannerAccessService;
import com.nuvemite.cms.planner.security.SecurityUtils;
import com.nuvemite.cms.planner.service.InspectionRequestService;
import com.nuvemite.cms.planner.web.dto.AddInspectionMessageRequest;
import com.nuvemite.cms.planner.web.dto.InspectionDateMessageResponse;
import com.nuvemite.cms.planner.web.dto.InspectionRequestResponse;
import com.nuvemite.cms.planner.web.dto.ProposeInspectionDateRequest;
import com.nuvemite.cms.planner.web.mapper.PlannerMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planner/inspection-requests")
public class InspectionRequestController {

    private final InspectionRequestService inspectionRequestService;
    private final PlannerAccessService access;
    private final PlannerMapper mapper;

    public InspectionRequestController(
            InspectionRequestService inspectionRequestService, PlannerAccessService access, PlannerMapper mapper) {
        this.inspectionRequestService = inspectionRequestService;
        this.access = access;
        this.mapper = mapper;
    }

    @GetMapping
    public Page<InspectionRequestResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) InspectionType inspectionType,
            @RequestParam(required = false) DateStatus dateStatus,
            @RequestParam(required = false) ResourceStatus resourceStatus,
            @PageableDefault(size = 20) Pageable pageable) {
        var user = SecurityUtils.currentUser();
        UUID filterCompany = companyId;
        boolean companyVisibleOnly = !user.isRegulator();
        if (!user.isRegulator()) {
            filterCompany = companyId != null ? companyId : user.companyIds().stream().findFirst().orElse(null);
        }
        return inspectionRequestService
                .list(filterCompany, companyVisibleOnly, inspectionType, dateStatus, resourceStatus, pageable)
                .map(mapper::toInspectionResponse);
    }

    @GetMapping("/{id}")
    public InspectionRequestResponse get(@PathVariable UUID id) {
        InspectionRequest request = inspectionRequestService.get(id);
        access.requireReadInspection(request);
        return mapper.toInspectionResponse(request);
    }

    @GetMapping("/{id}/messages")
    public List<InspectionDateMessageResponse> messages(@PathVariable UUID id) {
        InspectionRequest request = inspectionRequestService.get(id);
        access.requireReadInspection(request);
        return inspectionRequestService.messages(id).stream().map(mapper::toMessageResponse).toList();
    }

    @PostMapping("/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public InspectionDateMessageResponse addMessage(
            @PathVariable UUID id, @Valid @RequestBody AddInspectionMessageRequest body) {
        InspectionRequest request = inspectionRequestService.get(id);
        if (SecurityUtils.currentUser().isRegulator()) {
            access.requirePlannerInspection(request);
            return mapper.toMessageResponse(
                    inspectionRequestService.addPlannerMessage(id, SecurityUtils.currentSubject(), body));
        }
        access.requireCompanyInspection(request);
        return mapper.toMessageResponse(
                inspectionRequestService.addCompanyMessage(id, SecurityUtils.currentSubject(), body));
    }

    @PutMapping("/{id}/proposed-date")
    public InspectionRequestResponse proposeDate(
            @PathVariable UUID id, @Valid @RequestBody ProposeInspectionDateRequest body) {
        InspectionRequest request = inspectionRequestService.get(id);
        access.requirePlannerInspection(request);
        return mapper.toInspectionResponse(inspectionRequestService.proposeDate(id, body));
    }

    @PostMapping("/{id}/confirm-date")
    public InspectionRequestResponse confirmDate(@PathVariable UUID id) {
        InspectionRequest request = inspectionRequestService.get(id);
        access.requireCompanyInspection(request);
        return mapper.toInspectionResponse(inspectionRequestService.confirmDateByCompany(id));
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id) {
        InspectionRequest request = inspectionRequestService.get(id);
        access.requireRegulator();
        inspectionRequestService.cancel(id);
    }
}
