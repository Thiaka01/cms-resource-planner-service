package com.nuvemite.cms.planner.geo;

import java.util.List;

public record RouteMatrixRequest(List<LatLng> sources, List<LatLng> targets) {

    public record LatLng(double latitude, double longitude) {}
}
