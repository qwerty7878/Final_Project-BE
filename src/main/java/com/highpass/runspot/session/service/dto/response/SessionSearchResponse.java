package com.highpass.runspot.session.service.dto.response;

import com.highpass.runspot.session.domain.GenderPolicy;
import com.highpass.runspot.session.domain.RunType;
import com.highpass.runspot.session.domain.Session;
import java.math.BigDecimal;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SessionSearchResponse(
        Long id,
        String title,
        LocalDateTime startAt,
        String locationName,
        BigDecimal targetDistanceKm,
        Integer avgPaceSec,
        GenderPolicy genderPolicy,
        RunType runType
) {
    public static SessionSearchResponse from(Session session) {
        return SessionSearchResponse.builder()
                .id(session.getId())
                .title(session.getTitle())
                .startAt(session.getStartAt())
                .locationName(session.getLocationName())
                .targetDistanceKm(session.getTargetDistanceKm())
                .avgPaceSec(session.getAvgPaceSec())
                .genderPolicy(session.getGenderPolicy())
                .runType(session.getRunType())
                .build();
    }

}
