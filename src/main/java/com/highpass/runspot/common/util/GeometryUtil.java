package com.highpass.runspot.common.util;

import java.math.BigDecimal;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double KM_PER_LATITUDE_DEGREE = 111.0;
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point createPoint(final BigDecimal lat, final BigDecimal lon) {
        // x: 경도(longitude), y: 위도(latitude) 순서임
        return geometryFactory.createPoint(new Coordinate(lon.doubleValue(), lat.doubleValue()));
    }

    public static Polygon createBoundingBox(final BigDecimal lat, final BigDecimal lon, final double distanceKm) {
        final double latDegree = distanceKm / KM_PER_LATITUDE_DEGREE;
        final double lonDegree = distanceKm / (KM_PER_LATITUDE_DEGREE * Math.cos(Math.toRadians(lat.doubleValue())));

        final double minLat = lat.doubleValue() - latDegree;
        final double maxLat = lat.doubleValue() + latDegree;
        final double minLon = lon.doubleValue() - lonDegree;
        final double maxLon = lon.doubleValue() + lonDegree;

        return geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(minLon, minLat),
                new Coordinate(maxLon, minLat),
                new Coordinate(maxLon, maxLat),
                new Coordinate(minLon, maxLat),
                new Coordinate(minLon, minLat)
        });
    }

    public static double calculateDistance(final double startLat, final double startLon, final double endLat, final double endLon) {
        final double deltaLatitude = Math.toRadians(endLat - startLat);
        final double deltaLongitude = Math.toRadians(endLon - startLon);

        final double haversineValue = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat))
                * Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);

        final double angularDistance = 2 * Math.atan2(Math.sqrt(haversineValue), Math.sqrt(1 - haversineValue));

        return EARTH_RADIUS_KM * angularDistance * 1000; // convert to meters
    }
}
