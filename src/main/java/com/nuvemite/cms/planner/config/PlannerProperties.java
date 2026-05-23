package com.nuvemite.cms.planner.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cms.planner")
public record PlannerProperties(Outbox outbox, int workingDayMinutes, Geoapify geoapify) {

    public record Outbox(long pollIntervalMs, int batchSize) {}

    public record Geoapify(String apiKey, String baseUrl, int neighborTopN, String neighborJobCron) {}
}
