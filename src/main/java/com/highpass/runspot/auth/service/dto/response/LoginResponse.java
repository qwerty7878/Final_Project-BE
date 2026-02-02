package com.highpass.runspot.auth.service.dto.response;

import com.highpass.runspot.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long userId;
    private String name;
    private String ageGroup;
    private String gender;
    private Integer weeklyRuns;
    private Integer avgPaceMinPerKm;
    private BigDecimal mannerTemp;

    public static LoginResponse from(User user) {
        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .ageGroup(user.getAgeGroup().getCode())
                .gender(user.getGender().name())
                .weeklyRuns(user.getWeeklyRunningGoal())
                .avgPaceMinPerKm(formatPaceToInteger(user.getPacePreferenceSec()))
                .mannerTemp(user.getMannerTemp())
                .build();
    }

    private static Integer formatPaceToInteger(Integer seconds) {
        if (seconds == null || seconds == 0) {
            return 0;
        }

        int minutes = seconds / 60;
        int secs = seconds % 60;

        return minutes * 100 + secs;
    }
}