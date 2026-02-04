package com.highpass.runspot.auth.service.dto.response;

import java.util.List;

public record RecentRunningsResponse(
        List<RecentRunningResponse> recentRunnings
) {
    public static RecentRunningsResponse of(List<RecentRunningResponse> runnings) {
        return new RecentRunningsResponse(runnings);
    }
}