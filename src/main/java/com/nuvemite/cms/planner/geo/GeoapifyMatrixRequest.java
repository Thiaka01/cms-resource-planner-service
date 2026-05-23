package com.nuvemite.cms.planner.geo;

import java.util.List;

public record GeoapifyMatrixRequest(String mode, List<Waypoint> sources, List<Waypoint> targets) {

    public record Waypoint(List<Double> location) {}

    public static Waypoint point(double longitude, double latitude) {
        return new Waypoint(List.of(longitude, latitude));
    }
}
