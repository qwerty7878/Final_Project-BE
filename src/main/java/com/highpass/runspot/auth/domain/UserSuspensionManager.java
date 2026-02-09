package com.highpass.runspot.auth.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class UserSuspensionManager {

    private Integer consecutiveNoShowCount; // 연속 노쇼 횟수
    private LocalDateTime suspensionEndDate; // 이용 제한 종료 시각
    private Integer suspensionCount; // 이용 제한 받은 횟수
    private Boolean isPermanentlyBanned; // 영구 정지 여부
    private BigDecimal mannerTemp; // 매너온도

    private static final BigDecimal MANNER_TEMP_INCREASE = new BigDecimal("0.5");
    private static final BigDecimal MANNER_TEMP_DECREASE = new BigDecimal("1.0");
    private static final BigDecimal MIN_MANNER_TEMP = new BigDecimal("30.0");
    private static final BigDecimal SESSION_CREATE_MIN_TEMP = new BigDecimal("36.5");
    private static final int SUSPENSION_DAYS = 3;
    private static final int NO_SHOW_THRESHOLD = 2; // 연속 노쇼 2회
    private static final int PERMANENT_BAN_THRESHOLD = 2; // 제재 2회

    public UserSuspensionManager(
            Integer consecutiveNoShowCount,
            LocalDateTime suspensionEndDate,
            Integer suspensionCount,
            Boolean isPermanentlyBanned,
            BigDecimal mannerTemp) {
        this.consecutiveNoShowCount = consecutiveNoShowCount;
        this.suspensionEndDate = suspensionEndDate;
        this.suspensionCount = suspensionCount;
        this.isPermanentlyBanned = isPermanentlyBanned;
        this.mannerTemp = mannerTemp;
    }

    // 초기 생성자
    public static UserSuspensionManager createNew(BigDecimal initialMannerTemp) {
        return new UserSuspensionManager(0, null, 0, false, initialMannerTemp);
    }

    // 영구 정지 확인
    public boolean isPermanentlyBanned() {
        return this.isPermanentlyBanned;
    }

    // 일시 정지 확인
    public boolean isSuspended() {
        return suspensionEndDate != null && LocalDateTime.now().isBefore(suspensionEndDate);
    }

    // 활동 가능 여부
    public boolean isActive() {
        return !isPermanentlyBanned && !isSuspended();
    }

    // 세션 개설 가능 여부 (매너온도만 체크, 거리/횟수는 외부에서)
    public boolean canCreateSessionByTemp() {
        return isActive() && mannerTemp.compareTo(SESSION_CREATE_MIN_TEMP) >= 0;
    }

    // 경고 상태 (노쇼 1회)
    public boolean hasWarning() {
        return consecutiveNoShowCount == 1;
    }

    // 제재 단계
    public SuspensionLevel getSuspensionLevel() {
        if (isPermanentlyBanned) {
            return SuspensionLevel.PERMANENT_BAN;
        }
        if (isSuspended()) {
            return suspensionCount == 1 ? SuspensionLevel.FIRST_SUSPENSION : SuspensionLevel.FINAL_SUSPENSION;
        }
        if (hasWarning()) {
            return SuspensionLevel.WARNING;
        }
        return SuspensionLevel.NORMAL;
    }

    // 노쇼 처리
    public void recordNoShow() {
        // 매너온도 감소
        BigDecimal newTemp = this.mannerTemp.subtract(MANNER_TEMP_DECREASE);
        if (newTemp.compareTo(MIN_MANNER_TEMP) < 0) {
            newTemp = MIN_MANNER_TEMP;
        }
        this.mannerTemp = newTemp;

        // 연속 노쇼 카운트 증가
        this.consecutiveNoShowCount++;

        // 2회 이상 노쇼 시 제재
        if (this.consecutiveNoShowCount >= NO_SHOW_THRESHOLD) {
            applySuspension();
        }
    }

    // 출석 처리
    public void recordAttendance() {
        // 매너온도 증가
        this.mannerTemp = this.mannerTemp.add(MANNER_TEMP_INCREASE);

        // 연속 노쇼 카운트 초기화
        this.consecutiveNoShowCount = 0;
    }

    // 제재 적용
    private void applySuspension() {
        this.suspensionEndDate = LocalDateTime.now().plusDays(SUSPENSION_DAYS);
        this.suspensionCount++;

        // 2회 이상 제재 받으면 영구 정지
        if (this.suspensionCount >= PERMANENT_BAN_THRESHOLD) {
            this.isPermanentlyBanned = true;
        }
    }

    // 일시 정지 해제 (영구 정지는 관리자만)
    public void clearTemporarySuspension() {
        this.suspensionEndDate = null;
        this.consecutiveNoShowCount = 0;
    }

    // 제재 레벨 Enum
    public enum SuspensionLevel {
        NORMAL("정상"),
        WARNING("경고(노쇼 1회)"),
        FIRST_SUSPENSION("1차 이용 제한"),
        FINAL_SUSPENSION("2차 이용 제한(최종)"),
        PERMANENT_BAN("영구 정지");

        private final String description;

        SuspensionLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}