package com.highpass.runspot.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username; // 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false, length = 30)
    private String name; // 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group", nullable = false)
    private AgeGroup ageGroup; // 나이대

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; // 성별

    @Column(name = "manner_temp", nullable = false, precision = 4, scale = 1)
    @Builder.Default
    private BigDecimal mannerTemp = new BigDecimal("36.5"); // 기본 36.5

    @Column(name = "weekly_running_goal", nullable = false)
    @Builder.Default
    private Integer weeklyRunningGoal = 3; // 주간 러닝(설정값)

    @Column(name = "pace_preference_sec", nullable = false)
    @Builder.Default
    private Integer pacePreferenceSec = 360; // 평균 페이스(설정값, 초/km)
}
