package com.highpass.runspot.session.service.dto.request;

import jakarta.validation.constraints.Size;

public record SessionJoinRequest(
    @Size(max = 100, message = "메시지는 100자 이내로 작성해주세요.")
    String messageToHost
) {
}
