package com.highpass.runspot.session.service.dto.response;

import java.math.BigDecimal;

public record SessionMarkerSearchResponse(
        Long id,
        String title,
        BigDecimal x,
        BigDecimal y
) {
}
