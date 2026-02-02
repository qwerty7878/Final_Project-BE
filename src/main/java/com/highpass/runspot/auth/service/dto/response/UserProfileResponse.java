package com.highpass.runspot.auth.service.dto.response;

import com.highpass.runspot.auth.domain.AgeGroup;
import com.highpass.runspot.auth.domain.Gender;
import com.highpass.runspot.auth.domain.User;
import com.highpass.runspot.auth.domain.UserRunningStats;
import java.math.BigDecimal;

public record UserProfileResponse(
        Long userId,
        String name,
        AgeGroup ageGroup,
        Gender gender,
        Integer weeklyRuns,
        String avgPaceMinPerKm,
        BigDecimal mannerTemp,
        Integer totalRuns,
        BigDecimal totalDistanceKm
) {
    public static UserProfileResponse from(User user, UserRunningStats stats) {
        // 초/km를 분:초 형식으로 변환
        String avgPace = formatPace(user.getPacePreferenceSec());

        Integer totalRuns = stats != null ? stats.getTotalRunningCount() : 0;
        BigDecimal totalDistance = stats != null ? stats.getTotalDistanceKm() : BigDecimal.ZERO;

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getAgeGroup(),
                user.getGender(),
                user.getWeeklyRunningGoal(),
                avgPace,
                user.getMannerTemp(),
                totalRuns,
                totalDistance
        );
    }

    private static String formatPace(Integer seconds) {
        if (seconds == null) {
            return "6:00";
        }
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
}