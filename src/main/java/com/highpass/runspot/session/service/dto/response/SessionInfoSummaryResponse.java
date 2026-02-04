package com.highpass.runspot.session.service.dto.response;

import com.highpass.runspot.session.domain.GenderPolicy;
import com.highpass.runspot.session.domain.RunType;
import com.highpass.runspot.session.domain.Session;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SessionInfoSummaryResponse(
        Long id,
        String title,
        LocalDateTime startAt,
        String locationName,
        BigDecimal targetDistanceKm,
        Integer avgPaceSec,
        GenderPolicy genderPolicy,
        RunType runType
) {
    public static SessionInfoSummaryResponse from(Session session) {
        return new SessionInfoSummaryResponse(
                session.getId(),
                session.getTitle(),
                session.getStartAt(),
                session.getLocationName(),
                session.getTargetDistanceKm(),
                session.getAvgPaceSec(),
                session.getGenderPolicy(),
                session.getRunType()
        );
    }
}
