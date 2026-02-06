package com.highpass.runspot.auth.service.dto.response;

import com.highpass.runspot.session.domain.GenderPolicy;
import com.highpass.runspot.session.domain.RunType;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MyCreatedRunningsResponse(
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
        Integer currentParticipants,  // 승인된 참여 인원
        GenderPolicy genderPolicy,
        SessionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MyCreatedRunningsResponse from(Session session, int currentParticipants) {
        return new MyCreatedRunningsResponse(
                session.getId(),
                session.getHostUser().getId(),
                session.getTitle(),
                session.getRunType(),
                session.getLocationName(),
                BigDecimal.valueOf(session.getLocation().getX()),
                BigDecimal.valueOf(session.getLocation().getY()),
                session.getRoutePolyline(),
                session.getTargetDistanceKm(),
                session.getAvgPaceSec(),
                session.getStartAt(),
                session.getCapacity(),
                currentParticipants,
                session.getGenderPolicy(),
                session.getStatus(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }
}
