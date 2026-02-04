package com.highpass.runspot.auth.service.dto.response;

import com.highpass.runspot.session.domain.ParticipationStatus;
import com.highpass.runspot.session.domain.SessionParticipant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record AppliedRunningResponse(
        Long runningId,
        String title,
        String date,
        String time,
        ParticipationStatus approveStatus,
        Boolean chatEnabled
) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public static AppliedRunningResponse from(SessionParticipant participant) {
        LocalDateTime startAt = participant.getSession().getStartAt();

        return new AppliedRunningResponse(
                participant.getSession().getId(),
                participant.getSession().getTitle(),
                startAt.format(DATE_FORMATTER),
                startAt.format(TIME_FORMATTER),
                participant.getStatus(),
                participant.getStatus() == ParticipationStatus.APPROVED
        );
    }
}