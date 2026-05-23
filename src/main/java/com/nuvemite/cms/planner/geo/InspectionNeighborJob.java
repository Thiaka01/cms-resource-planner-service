package com.nuvemite.cms.planner.geo;

import com.nuvemite.cms.planner.repository.InspectionRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InspectionNeighborJob {

    private static final Logger log = LoggerFactory.getLogger(InspectionNeighborJob.class);

    private final InspectionRequestRepository inspectionRequestRepository;
    private final InspectionNeighborService neighborService;

    public InspectionNeighborJob(
            InspectionRequestRepository inspectionRequestRepository, InspectionNeighborService neighborService) {
        this.inspectionRequestRepository = inspectionRequestRepository;
        this.neighborService = neighborService;
    }

    @Scheduled(cron = "${cms.planner.geoapify.neighbor-job-cron:0 */30 * * * *}")
    public void computeNeighbors() {
        var requests = inspectionRequestRepository.findReadyForPlanning();
        if (requests.isEmpty()) {
            return;
        }
        log.info("Neighbor job: refreshing ranks for {} requests", requests.size());
        neighborService.refreshNeighborsForRequests(requests);
    }
}
