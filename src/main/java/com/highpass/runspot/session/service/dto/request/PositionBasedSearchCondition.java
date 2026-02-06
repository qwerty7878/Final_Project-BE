package com.highpass.runspot.session.service.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PositionBasedSearchCondition(
        @NotNull(message = "경도는 비어있을 수 없습니다")
        @DecimalMin(value = "-180", message = "경도는 -180 이상이어야 합니다")
        @DecimalMax(value = "180", message = "경도는 180 이하여야 합니다")
        BigDecimal x,

        @NotNull(message = "위도는 비어있을 수 없습니다")
        @DecimalMin(value = "-90", message = "위도는 -90보다 커야 합니다")
        @DecimalMax(value = "90", message = "위도는 90보다 작아야 합니다")
        BigDecimal y
) {
}
