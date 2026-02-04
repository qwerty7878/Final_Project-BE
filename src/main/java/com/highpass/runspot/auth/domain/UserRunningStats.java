package com.highpass.runspot.auth.domain;

import com.highpass.runspot.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_running_stats")
public class UserRunningStats extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_user_running_stats_user")
    )
    private User user;

    @Column(name = "total_running_count", nullable = false)
    @Builder.Default
    private Integer totalRunningCount = 0; // 총 러닝 횟수

    @Column(name = "total_distance_km", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalDistanceKm = BigDecimal.ZERO; // 누적 거리

    // 비즈니스 로직
    public void incrementRunning(BigDecimal distanceKm) {
        this.totalRunningCount += 1;
        this.totalDistanceKm = this.totalDistanceKm.add(distanceKm);
    }
}