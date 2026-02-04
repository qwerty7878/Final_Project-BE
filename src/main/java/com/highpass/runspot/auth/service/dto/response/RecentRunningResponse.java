package com.highpass.runspot.auth.service.dto.response;

import com.highpass.runspot.session.domain.AttendanceStatus;
import com.highpass.runspot.session.domain.SessionParticipant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record RecentRunningResponse(
        Long runningId,
        String title,
        String date,
        AttendanceStatus resultStatus
) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static RecentRunningResponse from(SessionParticipant participant) {
        LocalDateTime startAt = participant.getSession().getStartAt();

        return new RecentRunningResponse(
                participant.getSession().getId(),
                participant.getSession().getTitle(),
                startAt.format(DATE_FORMATTER),
                participant.getAttendanceStatus()
        );
    }
}