package com.nuvemite.cms.planner.geo;

/** Haversine and travel-time estimates when Geoapify is unavailable. */
public final class GeoDistance {

    private static final double EARTH_RADIUS_METERS = 6_371_000;
    private static final double DEFAULT_SPEED_MPS = 13.89; // ~50 km/h

    private GeoDistance() {}

    public static int haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int) (EARTH_RADIUS_METERS * c);
    }

    public static int estimateTravelSeconds(int distanceMeters) {
        return (int) Math.ceil(distanceMeters / DEFAULT_SPEED_MPS);
    }
}
