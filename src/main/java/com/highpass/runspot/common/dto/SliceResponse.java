package com.highpass.runspot.common.dto;

import java.util.List;

public record SliceResponse<T>(
        List<T> content,
        Long nextCursorId,
        boolean hasNext
) {
    public static <T> SliceResponse<T> of(List<T> content, Long nextCursorId, boolean hasNext) {
        return new SliceResponse<>(content, nextCursorId, hasNext);
    }
}
