package com.highpass.runspot.session.service.dto.response;

import com.highpass.runspot.session.domain.GenderPolicy;
import com.highpass.runspot.session.domain.RunType;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionParticipant;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SessionInfoDetailResponse(
        Long id,
        String title,
        LocalDateTime startAt,
        String locationName,
        BigDecimal targetDistanceKm,
        Integer avgPaceSec,
        GenderPolicy genderPolicy,
        RunType runType,
        String hostName,
        BigDecimal hostMannerTemp,
        List<String> participants,
        List<Session.RoutePoint> routePolyline
) {
    public static SessionInfoDetailResponse from(Session session, List<SessionParticipant> participants) {
        return new SessionInfoDetailResponse(
                session.getId(),
                session.getTitle(),
                session.getStartAt(),
                session.getLocationName(),
                session.getTargetDistanceKm(),
                session.getAvgPaceSec(),
                session.getGenderPolicy(),
                session.getRunType(),
                session.getHostUser().getName(),
                session.getHostUser().getMannerTemp(),
                participants.stream()
                        .map(p -> p.getUser().getName())
                        .toList(),
                session.getRoutePolyline()
        );
    }
}
