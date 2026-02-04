package com.highpass.runspot.common.util;

import java.math.BigDecimal;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point createPoint(final BigDecimal lat, final BigDecimal lon) {
        // x: 경도(longitude), y: 위도(latitude) 순서임
        return geometryFactory.createPoint(new Coordinate(lon.doubleValue(), lat.doubleValue()));
    }

    public static Polygon createBoundingBox(final BigDecimal lat, final BigDecimal lon, final double distanceKm) {
        final double latDegree = distanceKm / 111.0;
        final double lonDegree = distanceKm / (111.0 * Math.cos(Math.toRadians(lat.doubleValue())));

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

    public static double calculateDistance(final double lat1, final double lon1, final double lat2, final double lon2) {
        final int R = 6371; // Earth radius in km
        final double latDistance = Math.toRadians(lat2 - lat1);
        final double lonDistance = Math.toRadians(lon2 - lon1);
        final double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // convert to meters
    }
}
