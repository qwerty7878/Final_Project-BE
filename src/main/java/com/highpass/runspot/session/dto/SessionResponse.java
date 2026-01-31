package com.highpass.runspot.session.dto;

import com.highpass.runspot.session.domain.GenderPolicy;
import com.highpass.runspot.session.domain.RunType;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SessionResponse(
    Long id,
    Long hostUserId,
    String title,
    RunType runType,
    String locationName,
    BigDecimal locationX,
    BigDecimal locationY,
    List<Session.RoutePoint> routePolyline,
    BigDecimal targetDistanceKm,
    Integer avgPaceSec,
    LocalDateTime startAt,
    Integer capacity,
    GenderPolicy genderPolicy,
    SessionStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static SessionResponse from(Session session) {
        return new SessionResponse(
            session.getId(),
            session.getHostUser().getId(),
            session.getTitle(),
            session.getRunType(),
            session.getLocationName(),
            session.getLocationX(),
            session.getLocationY(),
            session.getRoutePolyline(),
            session.getTargetDistanceKm(),
            session.getAvgPaceSec(),
            session.getStartAt(),
            session.getCapacity(),
            session.getGenderPolicy(),
            session.getStatus(),
            session.getCreatedAt(),
            session.getUpdatedAt()
        );
    }
}
