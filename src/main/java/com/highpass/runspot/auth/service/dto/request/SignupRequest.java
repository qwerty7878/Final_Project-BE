package com.highpass.runspot.auth.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.highpass.runspot.auth.domain.AgeGroup;
import com.highpass.runspot.auth.domain.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "아이디는 필수입니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotNull(message = "나이대는 필수입니다")
    private AgeGroup ageGroup;

    @NotNull(message = "성별은 필수입니다")
    private Gender gender;

    // 선택사항
    private Integer weeklyRuns;
    private Integer avgPaceMinPerKm;

    @JsonIgnore
    public Integer getAvgPaceInSeconds() {
        if (avgPaceMinPerKm == null) {
            return null;
        }
        // 뒤 2자리는 초
        int seconds = avgPaceMinPerKm % 100;

        // 나머지는 분
        int minutes = avgPaceMinPerKm / 100;

        // 초가 60 이상이면 에러
        if (seconds >= 60) {
            throw new IllegalArgumentException("페이스 형식이 올바르지 않습니다. 초는 0~59 사이여야 합니다. (입력값: " + avgPaceMinPerKm + ")");
        }

        // 분이 음수이면 에러
        if (minutes < 0) {
            throw new IllegalArgumentException("페이스 형식이 올바르지 않습니다. (입력값: " + avgPaceMinPerKm + ")");
        }

        return minutes * 60 + seconds;
    }
}