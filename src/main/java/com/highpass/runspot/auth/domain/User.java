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
import java.time.LocalDateTime;
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

    // 제재 관리 필드들
    @Column(name = "consecutive_no_show_count", nullable = false)
    @Builder.Default
    private Integer consecutiveNoShowCount = 0; // 연속 노쇼 횟수

    @Column(name = "suspension_end_date")
    private LocalDateTime suspensionEndDate; // 이용 제한 종료 시각

    @Column(name = "suspension_count", nullable = false)
    @Builder.Default
    private Integer suspensionCount = 0; // 이용 제한 받은 횟수

    @Column(name = "is_permanently_banned", nullable = false)
    @Builder.Default
    private Boolean isPermanentlyBanned = false; // 영구 정지 여부

    // UserSuspensionManager 생성
    private UserSuspensionManager getSuspensionManager() {
        return new UserSuspensionManager(
                consecutiveNoShowCount,
                suspensionEndDate,
                suspensionCount,
                isPermanentlyBanned,
                mannerTemp
        );
    }

    // 상태 업데이트
    private void updateFromManager(UserSuspensionManager manager) {
        this.consecutiveNoShowCount = manager.getConsecutiveNoShowCount();
        this.suspensionEndDate = manager.getSuspensionEndDate();
        this.suspensionCount = manager.getSuspensionCount();
        this.isPermanentlyBanned = manager.getIsPermanentlyBanned();
        this.mannerTemp = manager.getMannerTemp();
    }

    // 위임 메서드들

    public boolean isPermanentlyBanned() {
        return getSuspensionManager().isPermanentlyBanned();
    }

    public boolean isSuspended() {
        return getSuspensionManager().isSuspended();
    }

    public boolean isActive() {
        return getSuspensionManager().isActive();
    }

    public boolean canCreateSession() {
        return getSuspensionManager().canCreateSessionByTemp();
    }

    public boolean hasWarning() {
        return getSuspensionManager().hasWarning();
    }

    public UserSuspensionManager.SuspensionLevel getSuspensionLevel() {
        return getSuspensionManager().getSuspensionLevel();
    }

    //상태 변경 메서드

    public void recordNoShow() {
        UserSuspensionManager manager = getSuspensionManager();
        manager.recordNoShow();
        updateFromManager(manager);
    }

    public void recordAttendance() {
        UserSuspensionManager manager = getSuspensionManager();
        manager.recordAttendance();
        updateFromManager(manager);
    }

    public void clearSuspension() {
        UserSuspensionManager manager = getSuspensionManager();
        manager.clearTemporarySuspension();
        updateFromManager(manager);
    }

    // 제재 횟수 조회 (에러 메시지용)
    public Integer getSuspensionCount() {
        return suspensionCount;
    }

    public LocalDateTime getSuspensionEndDate() {
        return suspensionEndDate;
    }
}
