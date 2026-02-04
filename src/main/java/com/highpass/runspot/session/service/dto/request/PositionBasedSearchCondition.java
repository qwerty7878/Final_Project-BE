package com.highpass.runspot.session.service.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PositionBasedSearchCondition(
        @NotNull(message = "경도는 비어있을 수 없습니다")
        BigDecimal lng,
        @NotNull(message = "위도는 비어있을 수 없습니다")
        BigDecimal lat
) {
}
