package com.highpass.runspot.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeGroup {
    TENS("10S", "10대"),
    TWENTIES("20S", "20대"),
    THIRTIES("30S", "30대"),
    FORTIES("40S", "40대"),
    FIFTIES("50S", "50대"),
    SIXTIES_PLUS("60S", "60대 이상");

    private final String code;
    private final String description;

    @JsonValue
    public String toJson() {
        return code;
    }

    @JsonCreator
    public static AgeGroup from(String value) {
        if (value == null) return null;

        String v = value.trim();

        for (AgeGroup g : values()) {
            if (g.code.equalsIgnoreCase(v)) return g;
        }

        try {
            return AgeGroup.valueOf(v.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바르지 않은 나이대입니다: " + value);
        }
    }
}
