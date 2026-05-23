package com.nuvemite.cms.planner.geo;

public record RouteMatrixResult(boolean geoapifyUsed, int[][] durationSeconds, int[][] distanceMeters) {

    public static RouteMatrixResult unavailable() {
        return new RouteMatrixResult(false, new int[0][0], new int[0][0]);
    }

    public int durationSeconds(int sourceIndex, int targetIndex) {
        if (durationSeconds.length == 0) {
            return 0;
        }
        return durationSeconds[sourceIndex][targetIndex];
    }

    public int distanceMeters(int sourceIndex, int targetIndex) {
        if (distanceMeters.length == 0) {
            return 0;
        }
        return distanceMeters[sourceIndex][targetIndex];
    }
}
