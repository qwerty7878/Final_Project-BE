package com.highpass.runspot.common.util;

import java.math.BigDecimal;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point createPoint(BigDecimal lat, BigDecimal lon) {
        // x: 경도(longitude), y: 위도(latitude) 순서임
        return geometryFactory.createPoint(new Coordinate(lon.doubleValue(), lat.doubleValue()));
    }
}
