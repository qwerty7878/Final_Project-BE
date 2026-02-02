package com.highpass.runspot.auth.service.dto.response;

import java.util.List;

public record AppliedRunningsResponse(
        List<AppliedRunningResponse> appliedRunnings
) {
    public static AppliedRunningsResponse of(List<AppliedRunningResponse> runnings) {
        return new AppliedRunningsResponse(runnings);
    }
}
