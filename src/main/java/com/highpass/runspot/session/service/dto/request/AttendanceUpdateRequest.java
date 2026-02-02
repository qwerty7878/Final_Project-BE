package com.highpass.runspot.session.service.dto.request;

import com.highpass.runspot.session.domain.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record AttendanceUpdateRequest(
    @NotNull(message = "출석 상태는 필수입니다.")
    AttendanceStatus attendanceStatus
) {
}
