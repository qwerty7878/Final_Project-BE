package com.highpass.runspot.session.service.dto.response;

import com.highpass.runspot.session.domain.GenderPolicy;
import com.highpass.runspot.session.domain.RunType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SessionNearbySearchResponse(
        Long id,
        String title,
        Integer applicants,
        Integer maxCapacity,
        String locationName,
        BigDecimal distanceFromPositionKm,
        BigDecimal targetDistanceKm,
        Integer avgPaceSec,
        LocalDateTime startAt
) {
}
